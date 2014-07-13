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
