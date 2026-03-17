package me.kharique.heartclasses.commands;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.gui.HeartRecipesGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartRecipesCommand implements CommandExecutor {

    private final HeartClasses plugin;

    public HeartRecipesCommand(HeartClasses plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        Player p = (Player) sender;
        new HeartRecipesGui(plugin).open(p);
        return true;
    }
}
