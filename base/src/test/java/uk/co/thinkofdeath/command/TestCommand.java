package uk.co.thinkofdeath.command;

import org.junit.Test;
import uk.co.thinkofdeath.command.parsers.ArgumentParser;
import uk.co.thinkofdeath.command.parsers.EnumParser;
import uk.co.thinkofdeath.command.parsers.ParserException;
import uk.co.thinkofdeath.command.types.ArgumentValidator;
import uk.co.thinkofdeath.command.types.MaxLength;
import uk.co.thinkofdeath.command.types.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TestCommand {

    @Test
    public void registerBasic() {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {

            @Command("helloworld")
            public void helloWorld(String sender) {

            }

            @Command("command2")
            public void myCommand(String sender) {

            }

            @Command("helloworld")
            public void helloWorld(Integer sender) { // Different sender

            }
        });
    }

    @Test
    public void registerComplex() {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("this command is a lot of sub commands")
            public void subCommands(String sender) {

            }

            @Command("tell ? ?")
            public void tell(String sender, String target, String message) {

            }

            @Command("addandtell ? ? ?")
            public void addTell(String sender, int a, int b, String target) {

            }
        });
    }

    @Test(expected = CommandRegisterException.class)
    public void registerUnparsable() {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("hello ?")
            public void nope(String sender, Object o) {

            }
        });
    }

    @Test(expected = CommandRegisterException.class)
    public void registerIncorrectArgCount() {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("hello ?")
            public void nope(String sender) {

            }
        });
    }

    @Test(expected = CommandRegisterException.class)
    public void registerIncorrectArgCount2() {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("hello")
            public void nope(String sender, int arg) {

            }
        });
    }

    @Test
    public void executeSingle() throws CommandException {
        final AtomicInteger call = new AtomicInteger();
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("first")
            public void first(String sender) {
                assertEquals(sender, "Jim");
                assertEquals("Incorrect call", call.getAndIncrement(), 0);
            }

            @Command("second")
            public void second(Integer sender) {
                assertEquals(sender, (Integer) 5);
                assertEquals("Incorrect call", call.getAndIncrement(), 1);
            }

            @Command("third")
            public void third(String sender) {
                assertEquals(sender, "Jimmy");
                assertEquals("Incorrect call", call.getAndIncrement(), 2);
            }
        });
        commandManager.execute("Jim", "first");
        commandManager.execute(5, "second");
        commandManager.execute("Jimmy", "third");
    }

    @Test
    public void executeComplex() throws CommandException {
        final AtomicInteger call = new AtomicInteger();
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("test sub commands one")
            public void subCommands(String sender) {
                assertEquals(sender, "bob");
                assertEquals("Incorrect call", call.getAndIncrement(), 0);
            }

            @Command("test sub commands two")
            public void subCommands2(String sender) {
                assertEquals(sender, "bob");
                assertEquals("Incorrect call", call.getAndIncrement(), 1);
            }

            @Command("give ? ?")
            public void arguments(String sender, String name, int money) {
                assertEquals(sender, "jimmy");
                assertEquals("Incorrect call", call.getAndIncrement(), 2);
                assertEquals(name, "timmy");
                assertEquals(money, 55);
            }

            @Command("give ? ~ ?")
            public void argumentsWithSub(String sender, String name, int money) {
                assertEquals(sender, "jimmy");
                assertEquals("Incorrect call", call.getAndIncrement(), 3);
                assertEquals(name, "timmy");
                assertEquals(money, 55);
            }
        });

        commandManager.execute("bob", "test sub commands one");
        commandManager.execute("bob", "test sub commands two");
        commandManager.execute("jimmy", "give timmy 55");
        commandManager.execute("jimmy", "give timmy ~ 55");
    }

    @Test(expected = CommandException.class)
    public void noCommand() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
        });
        commandManager.execute("hello", "world");
    }

    @Test(expected = CommandException.class)
    public void noSubCommand() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world create")
            public void test(String sender) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world remove");
    }

    @Test(expected = CommandException.class)
    public void noSubCommand2() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world ? create")
            public void test(String sender, String name) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world testing remove");
    }

    @Test(expected = CommandException.class)
    public void wrongType() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world ? time set ?")
            public void test(String sender, String name, int newTime) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world testing time set cake");
    }

    @Test(expected = CommandException.class)
    public void limit() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world ? time set ?")
            public void test(String sender, @MaxLength(5) String name, int newTime) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world testing time set 55");
    }

    @Test(expected = CommandException.class)
    public void limitCaller() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world ? time set ?")
            public void test(@MaxLength(2) String sender, String name, int newTime) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world testing time set 55");
    }

    @Test()
    public void limitCustom() throws CommandException {
        final AtomicInteger call = new AtomicInteger();
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("testing ?")
            public void test(String sender, @NoK String test) {
                assertEquals(sender, "bob");
                assertEquals("Incorrect call", call.getAndIncrement(), 0);
                assertFalse(test.toLowerCase().contains("k"));
            }
        });
        commandManager.execute("bob", "testing test");
    }

    @Test(expected = CommandException.class)
    public void limitCustomFail() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("testing ?")
            public void test(String sender, @NoK String test) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("bob", "testing cake");
    }

    @TypeHandler(
            value = NoK.NoKHandler.class,
            clazz = String.class
    )
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface NoK {

        class NoKHandler implements ArgumentValidator<String> {

            NoKHandler(NoK noK) {
            }

            @Override
            public String validate(String argument) {
                if (argument.toLowerCase().contains("k")) {
                    return "No K's allowed";
                }
                return null;
            }
        }
    }

    @Test
    public void customParser() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.addParser(int[].class, new ArgumentParser<int[]>() {
            @Override
            public int[] parse(String argument) throws ParserException {
                String[] args = argument.split("\\s*,\\s*");
                int[] out = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        out[i] = Integer.parseInt(args[i]);
                    } catch (NumberFormatException e) {
                        throw new ParserException(e.getMessage());
                    }
                }
                return out;
            }

            @Override
            public Set<int[]> complete(String argument) {
                return new HashSet<>();
            }
        });
        commandManager.register(new CommandHandler() {
            @Command("numbers ?")
            public void giveNumbers(String caller, int[] numbers) {
                assertEquals(caller, "tester");
                assertArrayEquals(numbers, new int[]{1, 2, 3, 4, 10});
            }
        });
        commandManager.execute("tester", "numbers `1, 2, 3, 4, 10`");
    }

    static enum TestEnum {
        HELLO,
        TESTING,
        CAKE,
        ABC
    }

    @Test
    public void enumParser() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.addParser(TestEnum.class, new EnumParser<>(TestEnum.class));
        commandManager.register(new CommandHandler() {
            private int count = 0;

            @Command("test ?")
            public void enumTest(String sender, TestEnum testEnum) {
                assertEquals(TestEnum.values()[count++], testEnum);
            }
        });

        commandManager.execute("test", "test hello");
        commandManager.execute("test", "test TESTING");
        commandManager.execute("test", "test cAkE");
        commandManager.execute("test", "test aBc");
        try {
            commandManager.execute("test", "test world");
            fail();
        } catch (CommandException e) {
            // All ok
        }
    }

    @Test
    public void differentCaller() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("hello")
            public void call(String caller) {

            }

            @Command("hello")
            public void call(Integer caller) {

            }
        });
        commandManager.execute("hey", "hello");
        commandManager.execute(5, "hello");
    }
}
