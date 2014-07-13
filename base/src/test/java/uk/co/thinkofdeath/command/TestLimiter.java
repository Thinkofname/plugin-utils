package uk.co.thinkofdeath.command;

import org.junit.Test;
import uk.co.thinkofdeath.command.types.ArgumentValidator;
import uk.co.thinkofdeath.command.types.MaxLength;
import uk.co.thinkofdeath.command.types.Range;
import uk.co.thinkofdeath.command.types.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@SuppressWarnings("unused")
public class TestLimiter {
    @Test(expected = CommandException.class)
    public void limitMaxLength() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            @Command("world ? time set ?")
            public void test(String sender, @MaxLength(5) String name, int newTime) {
                fail("Shouldn't be called");
            }
        });
        commandManager.execute("hello", "world testing time set 55");
    }

    public void limitRange() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            int call = 0;
            @Command("do ?")
            public void test(String sender, @Range(min = 0, max = 10) int val) {
                assertEquals(0, call++);
            }

            @Command("do ?")
            public void test2(String sender, @Range(min = 11, max = 20) int val) {
                assertEquals(1, call++);
            }

            @Command("do ?")
            public void test3(String sender, @Range(min = 21, max = 30) int val) {
                assertEquals(2, call++);
            }
        });
        commandManager.execute("hello", "do 4");
        commandManager.execute("hello", "do 18");
        commandManager.execute("hello", "do 25");
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
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            private int call = 0;
            @Command("testing ?")
            public void test(String sender, @NoK String test) {
                assertEquals(sender, "bob");
                assertEquals("Incorrect call", call++, 0);
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
            public CommandError validate(String argString, String argument) {
                if (argument.toLowerCase().contains("k")) {
                    return new CommandError(3, "No K's allowed");
                }
                return null;
            }
        }
    }
}
