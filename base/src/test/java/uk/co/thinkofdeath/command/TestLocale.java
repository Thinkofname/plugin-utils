package uk.co.thinkofdeath.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestLocale {

    @Test
    public void testCustomLocale() throws CommandException {
        CommandHandler handler = new CommandHandler() {

            @Command("my.command")
            public void myCommand(String caller, String arg1, int arg2) {
                assertEquals("tester", caller);
                assertEquals("hello", arg1);
                assertEquals(55, arg2);
            }

        };

        CommandManager cm1 = new CommandManager(new CommandLocaleHandler() {
            @Override
            public String getCommand(String command) {
                return "mycommand give ? ?";
            }

            @Override
            public String getError(CommandError error) {
                return error.getKey();
            }
        });
        CommandManager cm2 = new CommandManager(new CommandLocaleHandler() {
            @Override
            public String getCommand(String command) {
                return "testing ?2 give ?1";
            }

            @Override
            public String getError(CommandError error) {
                return error.getKey();
            }
        });

        cm1.register(handler);
        cm2.register(handler);

        cm1.execute("tester", "mycommand give hello 55");
        cm2.execute("tester", "testing 55 give hello");
    }
}
