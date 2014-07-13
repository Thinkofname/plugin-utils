package uk.co.thinkofdeath.command;

/**
 * Used to handle localizing of commands and errors
 */
public interface CommandLocaleHandler {

    /**
     * This is passed the command so that the handler
     * can process it and rearrange the order of
     * the arguments
     *
     * @param command
     *         The command syntax as passed to the
     *         {@link uk.co.thinkofdeath.command.Command}
     *         annotation
     * @return The new command syntax to use
     */
    public String getCommand(String command);

    /**
     * Returns the localised error message for the
     * error
     *
     * @param error
     *         The error containing the infomation
     *         needed to display the error
     * @return The localised string
     */
    public String getError(CommandError error);
}
