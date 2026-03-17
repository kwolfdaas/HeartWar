package me.kharique.heartclasses.commands;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.utils.HeartHelpBook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartHelpCommand implements CommandExecutor {

    private final HeartClasses plugin;

    public HeartHelpCommand(HeartClasses plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        Player p = (Player) sender;
        p.getInventory().addItem(HeartHelpBook.create(p));
        p.sendMessage("\u00A7aYou received a heart help book! Check your inventory.");
        return true;
    }
}
