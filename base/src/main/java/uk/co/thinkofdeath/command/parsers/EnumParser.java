package uk.co.thinkofdeath.command.parsers;

import java.util.HashSet;
import java.util.Set;

/**
 * Allows for enums to be used as arguments to commands
 *
 * @param <T>
 *         The type of enum
 */
public class EnumParser<T extends Enum<T>> implements ArgumentParser<T> {

    private final Class<T> e;

    /**
     * Creates an enum parser for the num
     *
     * @param e
     *         The class of the enum to use
     */
    public EnumParser(Class<T> e) {
        this.e = e;
    }

    @Override
    public T parse(String argument) throws ParserException {
        for (T v : e.getEnumConstants()) {
            if (v.name().equalsIgnoreCase(argument)) {
                return v;
            }
        }
        throw new ParserException("Unknown value '" + argument + "'");
    }

    @Override
    public Set<T> complete(String argument) {
        argument = argument.toUpperCase();
        HashSet<T> ret = new HashSet<>();
        for (T v : e.getEnumConstants()) {
            if (v.name().toUpperCase().startsWith(argument)) {
                ret.add(v);
            }
        }
        return ret;
    }
}
