package uk.co.thinkofdeath.command;

import java.util.HashMap;

/**
 * Provides a default set of strings for error messages
 */
public class DefaultLocaleHandler implements CommandLocaleHandler {

    protected HashMap<String, String> strings = new HashMap<>();
    {
        strings.put("parser.integer.invalid", "'%s' is not an integer");
        strings.put("parser.enum.invalid", "'%s' is not a valid value");
        strings.put("command.unknown", "Unknown command");
        strings.put("command.incorrect.caller", "You cannot call this command");
        strings.put("validator.maxlength", "'%s' is longer than the max %s");
        strings.put("validator.range.min", "'%d' must be greater or equal to '%d'");
        strings.put("validator.range.max", "'%d' must be lesser or equal to '%d'");
    }

    @Override
    public String getCommand(String command) {
        return command; // No processing needed
    }

    @Override
    public String getError(CommandError error) {
        if (!strings.containsKey(error.getKey())) {
            return error.getKey();
        }
        return String.format(strings.get(error.getKey()), error.getArgs());
    }
}
