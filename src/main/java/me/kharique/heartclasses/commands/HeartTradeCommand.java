package me.kharique.heartclasses.commands;

import me.kharique.heartclasses.HeartClasses;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartTradeCommand implements CommandExecutor {

    private final HeartClasses plugin;

    public HeartTradeCommand(HeartClasses plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used in-game.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Usage: /hearttrade <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("That player is not online.");
            return true;
        }

        plugin.getTradeManager().requestTrade(player, target);
        return true;
    }
}
