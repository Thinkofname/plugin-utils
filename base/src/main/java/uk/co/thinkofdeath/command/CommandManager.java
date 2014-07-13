/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.command;

import uk.co.thinkofdeath.command.parsers.ArgumentParser;
import uk.co.thinkofdeath.command.parsers.IntegerParser;
import uk.co.thinkofdeath.command.parsers.ParserException;
import uk.co.thinkofdeath.command.parsers.StringParser;
import uk.co.thinkofdeath.command.types.ArgumentValidator;
import uk.co.thinkofdeath.command.types.TypeHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A CommandManager handles registration and parsing
 * of commands as well as provided methods for completing
 * commands.
 */
public class CommandManager {

    private final CommandLocaleHandler localeHandler;
    private final CommandNode rootNode = new CommandNode();
    private final HashMap<Class<?>, ArgumentParser> parsers = new HashMap<>();
    private final Pattern splitter = Pattern.compile("(?:`(.*?)`)|(?:(.*?)(\\s|$))");

    /**
     * Creates a CommandManager initialised with parsers
     * for basic types:
     * <ul>
     * <li>String</li>
     * <li>int</li>
     * </ul>
     */
    public CommandManager() {
        this(new DefaultLocaleHandler());
    }

    /**
     * Creates a CommandManager initialised with parsers
     * for basic types:
     * <ul>
     * <li>String</li>
     * <li>int</li>
     * </ul>
     *
     * <p>
     *
     * This will use the passed locale handler to produce
     * error messages from commands. The locale handler
     * can also be used to pre-process commands be the
     * command manager registers them to allow for the
     * order of arguments to be changed
     *
     * @param localeHandler
     *         The locale handler to use
     */
    public CommandManager(CommandLocaleHandler localeHandler) {
        this.localeHandler = localeHandler;
        addParser(String.class, new StringParser());
        addParser(int.class, new IntegerParser());
    }

    /**
     * This registers all the commands contained in the
     * passed command handler class. Commands are methods
     * annotated with a {@link uk.co.thinkofdeath.command.Command}
     * annotation.
     *
     * <p>
     *
     * The value contained in the {@link uk.co.thinkofdeath.command.Command}
     * is used to find the placement of each argument in
     * the command as sub-commands are supported. The
     * format of the command value should be as follows
     *
     * <pre>
     * command sub ? anothersub ?
     * </pre>
     *
     * where `?` is the location of the parameter the
     * user can enter. The type of the parameters will be
     * based on the types of the parameters the Command
     * annotation is attached to excluding the first argument.
     * Only types with registered parsers may be used.
     *
     * <p>
     *
     * After `?` a number can follow to control which parameter
     * of the method this argument will be passed to. E.g. `?2`
     * will cause that argument to be assigned to the second
     * parameter (third including caller) of method. It is
     * recommended that if this is used for one argument
     * then the rest should also be explicitly defined as
     * well to ensure consistent results.
     *
     * <p>
     *
     * The first argument of the method will be the caller
     * and the command will only be passed to the method
     * if the caller is assignable to it.
     *
     * <p>
     *
     * Additional annotations may be added to the parameters
     * to impose limits on them. The annotation must have
     * a type handler annotation on them for the executor
     * use them. For example {@link uk.co.thinkofdeath.command.types.MaxLength}
     *
     * @param commandHandler
     *         The command handler to be added
     */
    public void register(CommandHandler commandHandler) {
        // We search through declared methods so that private
        // ones may be accessed. This allows for pure command
        // handling classes to be produced without exposing
        // the methods publicly (e.g. for a plugin with an
        // API)
        for (Method method : commandHandler.getClass().getDeclaredMethods()) {
            Command command = method.getAnnotation(Command.class);
            if (command == null) {
                continue;
            }
            method.setAccessible(true); // It may be private
            if (method.getParameterTypes().length < 1) {
                // No way to actually check if the argument is a caller
                // just that it exists since anything could technically
                // be caller
                throw new CommandRegisterException("You must have a 'caller' argument");
            }

            String[] args = localeHandler.getCommand(command.value()).split("\\s");
            Class<?>[] methodArgs = method.getParameterTypes();
            Annotation[][] methodArgAnnotations = method.getParameterAnnotations();
            int argIndex = 1; // Skip the 'caller' argument
            int[] argumentPositions = new int[methodArgs.length];

            // This starts at the at the root node and
            // searches/creates branches until it reaches
            // its end where it places the method to be
            // called later
            CommandNode currentNode = rootNode;
            for (String arg : args) {
                if (arg.startsWith("?")) { // Dynamic argument
                    int index;
                    if (arg.equals("?")) {
                        index = argIndex;
                    } else {
                        index = Integer.parseInt(arg.substring(1));
                        if (index >= methodArgs.length && index < 1) {
                            throw new CommandRegisterException("Invalid explicit argument position");
                        }
                    }
                    if (argIndex >= methodArgs.length) {
                        throw new CommandRegisterException("Incorrect number of method parameters");
                    }
                    Class<?> argType = methodArgs[index];
                    ArgumentParser parser = parsers.get(argType);
                    if (parser == null) {
                        throw new CommandRegisterException("No parser for " + argType.getSimpleName());
                    }
                    // Obtain the annotations with argument validators and create
                    // instances of them using the annotation as the arguments
                    Annotation[] annotations = methodArgAnnotations[index];
                    ArgumentValidator[] argCheckers = processCommandAnnotations(argType, annotations);
                    ArgumentNode argumentNode = new ArgumentNode(parser, argCheckers);
                    currentNode.arguments.add(argumentNode);
                    // Branch into the node
                    currentNode = argumentNode.node;
                    // Save the location of the argument
                    argumentPositions[argIndex] = index;
                    argIndex++;
                } else { // Constant
                    arg = arg.toLowerCase(); // We don't care about case for sub commands
                    if (!currentNode.subCommands.containsKey(arg)) {
                        // Creates the branch if it doesn't exist
                        currentNode.subCommands.put(arg, new CommandNode());
                    }
                    // Branch into the node
                    currentNode = currentNode.subCommands.get(arg);
                }
            }

            // Either we have left over '?' or not enough
            if (argIndex != methodArgs.length) {
                throw new CommandRegisterException("Incorrect number of method parameters");
            }

            // If we followed the route and got to a node with a method already then
            // another command has the same signature
            if (currentNode.methods.containsKey(methodArgs[0])) {
                throw new CommandRegisterException("Duplicate command");
            }

            ArgumentValidator[] argumentValidators = processCommandAnnotations(methodArgs[0],
                    methodArgAnnotations[0]);

            currentNode.methods.put(methodArgs[0],
                    new CommandNode.CommandMethod(
                            method,
                            commandHandler,
                            argumentValidators,
                            argumentPositions));
        }
    }

    // Obtains the annotations with argument validators and create
    // instances of them using the annotation as the arguments
    private ArgumentValidator[] processCommandAnnotations(Class<?> argType, Annotation[] annotations) {
        try {
            ArrayList<ArgumentValidator> argumentValidators = new ArrayList<>();
            for (Annotation annotation : annotations) {
                TypeHandler handler = annotation.annotationType().getAnnotation(TypeHandler.class);
                if (handler == null) {
                    continue;
                }
                if (!handler.clazz().isAssignableFrom(argType)) {
                    throw new RuntimeException(argType.getSimpleName() + " requires " + handler.clazz().getSimpleName());
                }
                Constructor<? extends ArgumentValidator> constructor = handler.value().getDeclaredConstructor(annotation.annotationType());
                constructor.setAccessible(true);
                ArgumentValidator type = constructor.newInstance(annotation);
                argumentValidators.add(type);

            }
            return argumentValidators.toArray(new ArgumentValidator[argumentValidators.size()]);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the command with the passed name as the passed caller
     * with the passed arguments (if any). This is a helper for APIs
     * that provide arguments as an array instead of the full string
     *
     * @param caller
     *         The caller to call as
     * @param name
     *         The name of the command
     * @param args
     *         The arguments of the command (if any)
     * @throws CommandException
     *         Thrown if the command failed to execute
     */
    public void execute(Object caller, String name, String... args) throws CommandException {
        execute(caller, join(name, args));
    }

    /**
     * Executes the command as the passed caller. Arguments
     * will be split by spaces unless the argument is wrapped
     * in '`' quotes
     *
     * @param caller
     *         The caller to call as
     * @param command
     *         The command
     * @throws CommandException
     *         Thrown if the command failed to execute
     */
    public void execute(Object caller, String command) throws CommandException {
        // lastError encountered whilst executing.
        CommandError lastError = null;
        String[] args = split(command);
        // Stores the states we can return to if the current route fails
        Stack<CommandState> toTry = new Stack<>();
        // The base arguments for the method which will always be the
        // caller
        ArrayList<Object> baseArgs = new ArrayList<>();
        baseArgs.add(caller);
        toTry.add(new CommandState(rootNode, baseArgs, 0));
        // Try every possible route until we match a command or
        // run out of options
        main:
        while (!toTry.isEmpty()) {
            CommandState state = toTry.pop();
            CommandNode currentNode = state.node;
            ArrayList<Object> arguments = state.arguments;
            int offset = state.offset;
            // We have enough arguments try executing the command
            if (offset == args.length) {
                if (currentNode.methods.size() == 0) {
                    // No command here
                    if (lastError == null || lastError.getPriority() < 1) {
                        lastError = new CommandError(1, "command.unknown");
                    }
                    continue;
                }
                // Check the caller
                callCheck:
                for (CommandNode.CommandMethod method : currentNode.methods.values()) {
                    Class<?> type = method.method.getParameterTypes()[0];
                    if (type.isAssignableFrom(caller.getClass())) {

                        for (ArgumentValidator t : method.argumentValidators) {
                            CommandError error = t.validate(null, caller);
                            if (error != null) {
                                if (lastError == null || lastError.getPriority() < error.getPriority()) {
                                    lastError = error;
                                }
                                continue callCheck;
                            }
                        }

                        Object[] processedArguments = new Object[arguments.size()];
                        for (int i = 0; i < processedArguments.length; i++) {
                            processedArguments[method.argumentPositions[i]] = arguments.get(i);
                        }

                        try {
                            method.method.invoke(method.owner, processedArguments);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    } else {
                        // Incorrect caller
                        if (lastError == null || lastError.getPriority() < 1) {
                            lastError = new CommandError(1, "command.incorrect.caller");
                        }
                    }
                }
                continue;
            }
            String arg = args[offset];
            String argLower = arg.toLowerCase(); // For checking sub commands
            // Try matching against all the argument types
            argTypes:
            for (ArgumentNode argumentNode : currentNode.arguments) {
                Object out;
                try {
                    out = argumentNode.parser.parse(arg);
                } catch (ParserException e) {
                    if (lastError == null || lastError.getPriority() < e.getError().getPriority()) {
                        lastError = e.getError();
                    }
                    continue;
                }
                if (out == null) {
                    continue;
                }
                for (ArgumentValidator type : argumentNode.type) {
                    CommandError error = type.validate(arg, out);
                    if (error != null) {
                        if (lastError == null || lastError.getPriority() < error.getPriority()) {
                            lastError = error;
                        }
                        continue argTypes;
                    }
                }
                ArrayList<Object> newArgs = (ArrayList<Object>) arguments.clone();
                newArgs.add(out);
                toTry.add(new CommandState(argumentNode.node, newArgs, offset + 1));
            }
            // Check sub-commands
            if (currentNode.subCommands.containsKey(argLower)) {
                CommandNode newNode = currentNode.subCommands.get(argLower);
                toTry.add(new CommandState(newNode, arguments, offset + 1));
            }
        }
        if (lastError == null || lastError.getPriority() < 1) {
            lastError = new CommandError(1, "command.unknown");
        }
        throw new CommandException(localeHandler.getError(lastError));
    }

    public List<String> complete(String name, String... args) {
        return complete(join(name, args));
    }

    public List<String> complete(String command) {
        Set<String> completions = new HashSet<>();
        String[] args = split(command);
        // Stores the states we can return to if the current route fails
        Stack<CommandState> toTry = new Stack<>();
        toTry.add(new CommandState(rootNode, null, 0));
        // Try every possible route until we match a command or
        // run out of options
        main:
        while (!toTry.isEmpty()) {
            CommandState state = toTry.pop();
            CommandNode currentNode = state.node;
            int offset = state.offset;

            String arg = args[offset];
            String argLower = arg.toLowerCase(); // For checking sub commands
            // We have enough arguments try completing the command
            if (offset == args.length - 1) {
                for (String sub : currentNode.subCommands.keySet()) {
                    if (sub.startsWith(argLower)) {
                        completions.add(sub);
                    }
                }

                for (ArgumentNode argumentNode : currentNode.arguments) {
                    completions.addAll(argumentNode.parser.complete(arg));
                }
                continue;
            }
            // Try matching against all the argument types
            argTypes:
            for (ArgumentNode argumentNode : currentNode.arguments) {
                Object out;
                try {
                    out = argumentNode.parser.parse(arg);
                } catch (ParserException e) {
                    continue;
                }
                if (out == null) {
                    continue;
                }
                for (ArgumentValidator type : argumentNode.type) {
                    CommandError error = type.validate(arg, out);
                    if (error != null) {
                        continue argTypes;
                    }
                }
                toTry.add(new CommandState(argumentNode.node, null, offset + 1));
            }
            // Check sub-commands
            if (currentNode.subCommands.containsKey(argLower)) {
                CommandNode newNode = currentNode.subCommands.get(argLower);
                toTry.add(new CommandState(newNode, null, offset + 1));
            }
        }
        return new ArrayList<>(completions);
    }

    /**
     * Defines a parser for the type class. The parser will be called
     * when the class is encountered as a parameter to a method.
     *
     * @param clazz
     *         The class to match this parser to
     * @param parser
     *         The parser to use for this type
     */
    public <T> void addParser(Class<T> clazz, ArgumentParser<T> parser) {
        parsers.put(clazz, parser);
    }

    // Split by spaces unless the argument is quoted with `
    private String[] split(String command) {
        Matcher matcher = splitter.matcher(command);
        ArrayList<String> args = new ArrayList<>();
        while (matcher.find()) {
            String arg = matcher.group().trim();
            if (arg.startsWith("`")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            args.add(arg);
        }
        args.remove(args.size() - 1);
        return args.toArray(new String[args.size()]);
    }

    // Helper for converting between split arguments and joined arguments
    // format
    private String join(String name, String... args) {
        StringBuilder out = new StringBuilder(name);
        for (String arg : args) {
            out.append(' ').append(arg);
        }
        return out.toString();
    }

    private static class CommandState {
        private CommandNode node;
        private ArrayList<Object> arguments;
        private int offset;

        private CommandState(CommandNode node, ArrayList<Object> arguments, int offset) {
            this.node = node;
            this.arguments = arguments;
            this.offset = offset;
        }
    }
}
