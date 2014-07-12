package uk.co.thinkofdeath.command;

/**
 * Thrown if a command fails to register
 */
public class CommandRegisterException extends RuntimeException {
    CommandRegisterException(String message) {
        super(message);
    }
}
