package uk.co.thinkofdeath.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("unused")
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
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            private int call = 0;

            @Command("first")
            public void first(String sender) {
                assertEquals(sender, "Jim");
                assertEquals("Incorrect call", call++, 0);
            }

            @Command("second")
            public void second(Integer sender) {
                assertEquals(sender, (Integer) 5);
                assertEquals("Incorrect call", call++, 1);
            }

            @Command("third")
            public void third(String sender) {
                assertEquals(sender, "Jimmy");
                assertEquals("Incorrect call", call++, 2);
            }
        });
        commandManager.execute("Jim", "first");
        commandManager.execute(5, "second");
        commandManager.execute("Jimmy", "third");
    }

    @Test
    public void executeComplex() throws CommandException {
        CommandManager commandManager = new CommandManager();
        commandManager.register(new CommandHandler() {
            private int call = 0;

            @Command("test sub commands one")
            public void subCommands(String sender) {
                assertEquals(sender, "bob");
                assertEquals("Incorrect call", call++, 0);
            }

            @Command("test sub commands two")
            public void subCommands2(String sender) {
                assertEquals(sender, "bob");
                assertEquals("Incorrect call", call++, 1);
            }

            @Command("give ? ?")
            public void arguments(String sender, String name, int money) {
                assertEquals(sender, "jimmy");
                assertEquals("Incorrect call", call++, 2);
                assertEquals(name, "timmy");
                assertEquals(money, 55);
            }

            @Command("give ? ~ ?")
            public void argumentsWithSub(String sender, String name, int money) {
                assertEquals(sender, "jimmy");
                assertEquals("Incorrect call", call++, 3);
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
