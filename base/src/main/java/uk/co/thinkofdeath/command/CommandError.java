package uk.co.thinkofdeath.command;

/**
 * Contains the information required to display a
 * error message
 */
public class CommandError {

    private final int priority;
    private final String key;
    private final Object[] args;

    public CommandError(int priority, String key, Object... args) {
        this.priority = priority;
        this.key = key;
        this.args = args;
    }

    /**
     * The priority of error, used in selecting which
     * error message to display
     *
     * @return The priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * The localization key for this error
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Any additional arguments required to display
     * the error
     *
     * @return The arguments
     */
    public Object[] getArgs() {
        return args;
    }
}
