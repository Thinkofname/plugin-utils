package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.entity.Player;
import uk.co.thinkofdeath.command.CommandError;
import uk.co.thinkofdeath.command.types.ArgumentValidator;
import uk.co.thinkofdeath.command.types.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TypeHandler(
        value = StrictHandler.class,
        clazz = Player.class
)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Requires the player to be an exact match instead of
 * a partial one
 */
public @interface Strict {
}


class StrictHandler implements ArgumentValidator<Player> {

    StrictHandler(Strict strict) {
    }

    @Override
    public CommandError validate(String argStr, Player argument) {
        if (argStr == null) return null;
        if (argument.getName().equalsIgnoreCase(argStr)) {
            return null;
        }
        return new CommandError(3, "bukkit.no-player", argStr);
    }
}