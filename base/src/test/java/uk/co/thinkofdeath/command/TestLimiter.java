package uk.co.thinkofdeath.command;

import org.junit.Test;
import uk.co.thinkofdeath.command.types.ArgumentValidator;
import uk.co.thinkofdeath.command.types.MaxLength;
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
            public String validate(String argument) {
                if (argument.toLowerCase().contains("k")) {
                    return "No K's allowed";
                }
                return null;
            }
        }
    }
}
