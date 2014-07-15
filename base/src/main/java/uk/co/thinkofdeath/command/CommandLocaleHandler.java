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
 * Used to handle localizing of commands and errors
 */
public interface CommandLocaleHandler {

    /**
     * This is passed the command so that the handler
     * can process it and rearrange the order of
     * the arguments
     *
     * @param command
     *         The command syntax as passed to the
     *         {@link uk.co.thinkofdeath.command.Command}
     *         annotation
     * @return The new command syntax to use
     */
    public String getCommand(String command);

    /**
     * Returns the localised error message for the
     * error
     *
     * @param error
     *         The error containing the information
     *         needed to display the error
     * @return The localised string
     */
    public String getError(CommandError error);
}
