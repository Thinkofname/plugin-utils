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

package uk.co.thinkofdeath.command.parsers;

import java.util.HashSet;
import java.util.Set;

/**
 * Allows for any valid integer
 */
public class IntegerParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(String argument) throws ParserException {
        try {
            return Integer.valueOf(argument);
        } catch (NumberFormatException e) {
            throw new ParserException("'" + argument + "' is not an integer");
        }
    }

    @Override
    public Set<Integer> complete(String argument) {
        return new HashSet<>();
    }
}
