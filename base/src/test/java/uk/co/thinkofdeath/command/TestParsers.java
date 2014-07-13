package uk.co.thinkofdeath.command;

import org.junit.Test;
import uk.co.thinkofdeath.command.parsers.ArgumentParser;
import uk.co.thinkofdeath.command.parsers.EnumParser;
import uk.co.thinkofdeath.command.parsers.ParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("unused")
public class TestParsers {

    @Test
    public void stringParser() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            private String[] expected = {"a", "hello", "TesTing", "123", "a2C"};
            private int current = 0;

            @Command("test ?")
            public void stringTest(String sender, String arg) {
                assertEquals(arg, expected[current++]);
            }
        });
        commandManager.execute("tester", "test a");
        commandManager.execute("tester", "test hello");
        commandManager.execute("tester", "test TesTing");
        commandManager.execute("tester", "test 123");
        commandManager.execute("tester", "test a2C");
    }

    @Test
    public void intParser() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            private int[] expected = {55, 1204, 36589, -3590, -123456};
            private int current = 0;
            @Command("test ?")
            public void intTest(String sender, int arg) {
                assertEquals(arg, expected[current++]);
            }
        });
        commandManager.execute("tester", "test 55");
        commandManager.execute("tester", "test 1204");
        commandManager.execute("tester", "test 36589");
        commandManager.execute("tester", "test -3590");
        commandManager.execute("tester", "test -123456");
    }

    @Test(expected = CommandException.class)
    public void intParserFail() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("test ?")
            public void intTest(String sender, int arg) {
                fail();
            }
        });
        commandManager.execute("tester", "test 55e3");
    }

    @Test(expected = CommandException.class)
    public void intWrongType() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world ? time set ?")
            public void test(String sender, String name, int newTime) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world testing time set cake");
    }

    static enum TestEnum {
        HELLO,
        TESTING,
        CAKE,
        COLD,
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
        commandManager.execute("test", "test cold");
        commandManager.execute("test", "test aBc");
        try {
            commandManager.execute("test", "test world");
            fail();
        } catch (CommandException e) {
            // All ok
        }
    }

    @Test
    public void enumParserComplete() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.addParser(TestEnum.class, new EnumParser<>(TestEnum.class));
        commandManager.register(new CommandHandler() {

            @Command("test ?")
            public void enumTest(String sender, TestEnum testEnum) {
                fail();
            }

            @Command("test abcd")
            public void test(String sender) {
                fail();
            }
        });
        Util.same(Arrays.asList("HELLO"), commandManager.complete("test hel"));
        Util.same(new ArrayList<String>(), commandManager.complete("test wor"));
        Util.same(Arrays.asList("CAKE", "COLD"), commandManager.complete("test c"));
        Util.same(Arrays.asList("TESTING"), commandManager.complete("test test"));
        Util.same(Arrays.asList("ABC", "abcd"), commandManager.complete("test ab"));
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
                        throw new ParserException(new CommandError(2, e.getMessage()));
                    }
                }
                return out;
            }

            @Override
            public Set<String> complete(String argument) {
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
}
