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
