package uk.co.thinkofdeath.command.types;

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
    public String validate(Integer argument) {
        if (argument < min) {
            return String.format("'%d' must be greater or equal to '%d'", argument, min);
        }
        if (argument < max) {
            return String.format("'%d' must be lesser or equal to '%d'", argument, max);
        }
        return null;
    }
}

