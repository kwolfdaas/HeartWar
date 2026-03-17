package me.kharique.heartclasses.gui;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.hearts.HeartDefinition;
import me.kharique.heartclasses.hearts.HeartRecipe;
import me.kharique.heartclasses.hearts.HeartType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HeartRecipesGui {

    private final HeartClasses plugin;

    public HeartRecipesGui(HeartClasses plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.RED + "Heart Recipes");

        int slot = 0;
        for (HeartDefinition def : plugin.getHeartManager().getAll()) {
            if (slot >= 45) break;
            ItemStack icon = plugin.getHeartManager().createHeartItem(def);
            inv.setItem(slot++, icon);
        }

        player.openInventory(inv);
    }

    public void openRecipe(Player player, HeartType type, boolean classic) {
        HeartDefinition def = plugin.getHeartManager().getDefinition(type);
        if (def == null) return;

        String title = classic ? ChatColor.RED + "Recipe (Classic)" : ChatColor.RED + "Recipe (Fancy)";
        Inventory inv = Bukkit.createInventory(null, 54, title);

        HeartRecipe recipe = plugin.getHeartManager().getRecipe(type);

        // Output (middle of inventory)
        ItemStack result = plugin.getHeartManager().createHeartItem(def);
        inv.setItem(24, result);

        // Classic 3x3 grid (slots 10-12, 19-21, 28-30)
        if (recipe != null) {
            int[] slots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
            int idx = 0;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    Material m = recipe.getGrid()[r][c];
                    if (m != null && m != Material.AIR) {
                        inv.setItem(slots[idx], new ItemStack(m));
                    }
                    idx++;
                }
            }
        }

        if (!classic) {
            ItemStack info = new ItemStack(Material.BOOK);
            ItemMeta meta = info.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Info");
            meta.setLore(java.util.Arrays.asList(
                    ChatColor.GRAY + def.getDescription(),
                    ChatColor.DARK_GRAY + "Type: " + def.getType().name(),
                    ChatColor.DARK_GRAY + "Tier: " + def.getTier()
            ));
            info.setItemMeta(meta);
            inv.setItem(40, info);
        }

        ItemStack toggle = new ItemStack(Material.NETHER_STAR);
        ItemMeta tMeta = toggle.getItemMeta();
        tMeta.setDisplayName(ChatColor.AQUA + (classic ? "Switch to Fancy" : "Switch to Classic"));
        toggle.setItemMeta(tMeta);
        inv.setItem(49, toggle);

        player.openInventory(inv);
    }
}
