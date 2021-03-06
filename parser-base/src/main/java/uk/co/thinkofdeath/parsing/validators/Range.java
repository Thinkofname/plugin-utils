/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.parsing.validators;

import uk.co.thinkofdeath.parsing.ParserException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TypeHandler(
        value = RangeHandler.class,
        clazz = int.class
)
@Target({ElementType.PARAMETER, ElementType.METHOD})
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
    public void validate(String argStr, Integer argument) throws ParserException {
        if (argument < min) {
            throw new ParserException(3, "validator.range.min", argument, min);
        }
        if (argument > max) {
            throw new ParserException(3, "validator.range.max'", argument, max);
        }
    }
}

