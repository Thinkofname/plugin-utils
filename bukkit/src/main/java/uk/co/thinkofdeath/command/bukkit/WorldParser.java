package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import uk.co.thinkofdeath.command.parsers.ArgumentParser;
import uk.co.thinkofdeath.command.parsers.ParserException;

import java.util.HashSet;
import java.util.Set;

/**
 * Parses world name's into World objects
 */
public class WorldParser implements ArgumentParser<World> {
    private final Plugin plugin;

    public WorldParser(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public World parse(String argument) throws ParserException {
        World world = plugin.getServer().getWorld(argument);
        if (world == null) {
            throw new ParserException("No such world '" + argument + "'");
        }
        return world;
    }

    @Override
    public Set<String> complete(String argument) {
        HashSet<String> completions = new HashSet<>();
        argument = argument.toLowerCase();
        for (World world : plugin.getServer().getWorlds()) {
            if (world.getName().toLowerCase().startsWith(argument)) {
                completions.add(world.getName());
            }
        }
        return completions;
    }
}
