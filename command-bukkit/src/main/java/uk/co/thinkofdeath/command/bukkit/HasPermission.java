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

package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.command.CommandSender;
import uk.co.thinkofdeath.command.CommandError;
import uk.co.thinkofdeath.command.validators.ArgumentValidator;
import uk.co.thinkofdeath.command.validators.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TypeHandler(
        value = HasPermissionHandler.class,
        clazz = CommandSender.class
)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Requires the player to have at least one of the permission nodes
 */
public @interface HasPermission {
    String[] value();

    boolean wildcard() default false;
}


class HasPermissionHandler implements ArgumentValidator<CommandSender> {

    private final String[] permissions;
    private final boolean wildcard;

    HasPermissionHandler(HasPermission hasPermission) {
        permissions = hasPermission.value();
        wildcard = hasPermission.wildcard();
    }

    @Override
    public CommandError validate(String argStr, CommandSender argument) {
        for (String permission : permissions) {
            // Check for the permission
            if (argument.hasPermission(permission)) {
                return null;
            }
            // If the permission was manually set to false
            // don't bother with the wildcards
            if (!argument.isPermissionSet(permission) && wildcard) {
                String perm = permission;
                while (perm.indexOf('.') != -1) {
                    perm = perm.substring(0, perm.lastIndexOf('.'));
                    if (argument.isPermissionSet(perm + ".*")) {
                        if (argument.hasPermission(perm + ".*")) {
                            return null;
                        } else {
                            return new CommandError(3, "bukkit.no-permission");
                        }
                    }
                }
            }
        }
        return new CommandError(3, "bukkit.no-permission");
    }
}