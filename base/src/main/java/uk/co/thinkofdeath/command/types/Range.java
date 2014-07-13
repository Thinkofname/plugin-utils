package uk.co.thinkofdeath.command.types;

import uk.co.thinkofdeath.command.CommandError;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TypeHandler(
        value = RangeHandler.class,
        clazz = int.class
)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Requires the integer to be between min and max
 */
public @interface Range {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
}

class RangeHandler implements ArgumentValidator<Integer> {

    private final int min;
    private final int max;

    RangeHandler(Range range) {
        min = range.min();
        max = range.max();
    }

    @Override
    public CommandError validate(String argStr, Integer argument) {
        if (argument < min) {
            return new CommandError(3, "validator.range.min", argument, min);
        }
        if (argument > max) {
            return new CommandError(3, "validator.range.max'", argument, max);
        }
        return null;
    }
}

