package me.kharique.heartclasses.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GodWeapon {

    public static ItemStack create() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "GOD SLAYER");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);

        sword.setItemMeta(meta);
        return sword;
    }
}
