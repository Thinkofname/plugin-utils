package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import uk.co.thinkofdeath.command.parsers.ArgumentParser;
import uk.co.thinkofdeath.command.parsers.ParserException;

import java.util.HashSet;
import java.util.Set;

/**
 * Parses player name's into Player objects. By default
 * this parsing doesn't require an exact match to work.
 * This may be changed by using {@link uk.co.thinkofdeath.command.bukkit.Strict}
 */
public class PlayerParser implements ArgumentParser<Player> {
    private final Plugin plugin;

    public PlayerParser(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Player parse(String argument) throws ParserException {
        return plugin.getServer().getPlayer(argument);
    }

    @Override
    public Set<String> complete(String argument) {
        HashSet<String> completions = new HashSet<>();
        for (Player player : plugin.getServer().matchPlayer(argument)) {
            completions.add(player.getName());
        }
        return completions;
    }
}
