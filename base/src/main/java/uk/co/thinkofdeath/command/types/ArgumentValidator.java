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

package uk.co.thinkofdeath.command.types;

/**
 * An argument validator is used to apply limits to
 * specific argument of a command. This must be used
 * in-conjunction with a {@link uk.co.thinkofdeath.command.types.TypeHandler}
 * annotation that points to this class.
 *
 * As well as having the validate method and implementing
 * class must have a constructor that takes the matching
 * annotation as the first and only parameter
 */
public interface ArgumentValidator<T> {

    /**
     * Checks if the argument matches the specification declared
     * in the matching annotation.
     *
     * @param argument The argument to be validated, should
     *                 be of the type declared in the type handler
     * @return null if it validates fine otherwise the
     *          error to be displayed
     */
    String validate(T argument);
}
