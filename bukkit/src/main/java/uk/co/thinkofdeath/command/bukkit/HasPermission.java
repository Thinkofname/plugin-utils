package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.command.CommandSender;
import uk.co.thinkofdeath.command.CommandError;
import uk.co.thinkofdeath.command.types.ArgumentValidator;
import uk.co.thinkofdeath.command.types.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TypeHandler(
        value = HasPermissionHandler.class,
        clazz = CommandSender.class
)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Requires the player to have the permission node
 */
public @interface HasPermission {
    String value();
}


class HasPermissionHandler implements ArgumentValidator<CommandSender> {

    private final String permission;

    HasPermissionHandler(HasPermission hasPermission) {
        permission = hasPermission.value();
    }

    @Override
    public CommandError validate(String argStr, CommandSender argument) {
        if (argument.hasPermission(permission)) {
            return null;
        }
        return new CommandError(3, "bukkit.no-permission");
    }
}