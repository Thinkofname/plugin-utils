package uk.co.thinkofdeath.command.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import uk.co.thinkofdeath.command.CommandException;
import uk.co.thinkofdeath.command.CommandManager;
import uk.co.thinkofdeath.command.parsers.EnumParser;

import java.util.List;

/**
 * BukkitCommandManager provides the standard types
 * from {@link uk.co.thinkofdeath.command.CommandManager}
 * as well as some Bukkit specific types.
 *
 * <p>
 *
 * For ease of use this also implements
 * {@link org.bukkit.command.TabExecutor} so that
 * it can be simply dropped into place
 */
public class BukkitCommandManager extends CommandManager implements TabExecutor {

    /**
     * Creates a CommandManager initialised with parsers
     * from the CommandManager as well as:
     * <ul>
     * <li>{@link org.bukkit.World} with {@link uk.co.thinkofdeath.command.bukkit.WorldParser}</li>
     * <li>{@link org.bukkit.entity.Player} with {@link uk.co.thinkofdeath.command.bukkit.PlayerParser}</li>
     * <li>{@link org.bukkit.Material} with {@link uk.co.thinkofdeath.command.parsers.EnumParser}</li>
     * </ul>
     *
     * @param plugin
     *         The plugin which owns this command
     *         manager
     */
    public BukkitCommandManager(Plugin plugin) {
        super();
        addParser(World.class, new WorldParser(plugin));
        addParser(Player.class, new PlayerParser(plugin));
        addParser(Material.class, new EnumParser<>(Material.class));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        try {
            execute(commandSender, command.getName(), args);
        } catch (CommandException e) {
            commandSender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        return complete(command.getName(), args);
    }
}
