package me.kharique.heartclasses.commands;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.data.PlayerData;
import me.kharique.heartclasses.data.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartsCommand implements CommandExecutor {

    private final HeartClasses plugin;

    public HeartsCommand(HeartClasses plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        Player p = (Player) sender;
        PlayerDataManager pdm = plugin.getPlayerDataManager();
        PlayerData data = pdm.getOrCreate(p.getUniqueId());

        p.sendMessage(ChatColor.RED + "Heart Type: " + ChatColor.WHITE + data.getHeartType().name());
        p.sendMessage(ChatColor.RED + "Hearts: " + ChatColor.WHITE + data.getHearts());
        return true;
    }
}
