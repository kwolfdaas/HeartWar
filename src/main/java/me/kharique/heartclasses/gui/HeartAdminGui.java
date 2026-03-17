package me.kharique.heartclasses.gui;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.hearts.HeartDefinition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HeartAdminGui {

    private final HeartClasses plugin;

    public HeartAdminGui(HeartClasses plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "Heart Admin");

        int slot = 0;
        for (HeartDefinition def : plugin.getHeartManager().getAll()) {
            if (slot >= 45) break;
            ItemStack icon = plugin.getHeartManager().createHeartItem(def);
            inv.setItem(slot++, icon);
        }

        player.openInventory(inv);
    }
}
