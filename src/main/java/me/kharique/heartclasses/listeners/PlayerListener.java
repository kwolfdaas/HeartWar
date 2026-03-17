package me.kharique.heartclasses.listeners;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.abilities.HeartAbility;
import me.kharique.heartclasses.data.PlayerData;
import me.kharique.heartclasses.data.PlayerDataManager;
import me.kharique.heartclasses.gui.HeartRecipesGui;
import me.kharique.heartclasses.hearts.HeartDefinition;
import me.kharique.heartclasses.hearts.HeartManager;
import me.kharique.heartclasses.hearts.HeartType;
import me.kharique.heartclasses.trade.HeartTradeManager;
import me.kharique.heartclasses.utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {

    private final HeartClasses plugin;

    public PlayerListener(HeartClasses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerDataManager pdm = plugin.getPlayerDataManager();
        PlayerData data = pdm.getOrCreate(p.getUniqueId());

        // Give physical heart item (sync)
        HeartManager hm = plugin.getHeartManager();
        HeartDefinition def = hm.getDefinition(data.getHeartType());
        ItemStack heartItem = hm.createHeartItem(def);
        if (!p.getInventory().containsAtLeast(heartItem, 1)) {
            p.getInventory().addItem(heartItem);
        }

        plugin.getPlayerDataManager().tryUnlockGodHeart(p, data);
        maybeApplyScoreboard(p, data);

        // Deliver the help book on join (one copy only)
        if (!p.getInventory().contains(Material.WRITTEN_BOOK)) {
            p.getInventory().addItem(me.kharique.heartclasses.utils.HeartHelpBook.create(p));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(p.getUniqueId());
        HeartAbility ability = plugin.getAbilityManager().get(data.getHeartType());
        if (ability != null) {
            ability.onRightClick(p);
        }

        // Update heart based on held or inventory heart items
        refreshHeartFromInventory(p);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        refreshHeartFromInventory(p);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        plugin.getTradeManager().onInventoryClose(e);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        World w = victim.getWorld();
        if (!w.getName().equalsIgnoreCase("heart")) return;

        PlayerDataManager pdm = plugin.getPlayerDataManager();
        PlayerData vData = pdm.getOrCreate(victim.getUniqueId());
        PlayerData kData = pdm.getOrCreate(killer.getUniqueId());

        if (vData.getHearts() <= 0) return;

        vData.removeHearts(1);
        kData.addHearts(1);

        // Transfer type (simple version: killer gets victim's type)
        kData.setHeartType(vData.getHeartType());

        // Give killer a physical heart item
        HeartManager hm = plugin.getHeartManager();
        HeartDefinition def = hm.getDefinition(kData.getHeartType());
        if (def != null) {
            killer.getInventory().addItem(hm.createHeartItem(def));
        }

        HeartAbility ability = plugin.getAbilityManager().get(kData.getHeartType());
        if (ability != null) {
            ability.onKill(killer, victim);
        }

        plugin.getPlayerDataManager().tryUnlockGodHeart(killer, kData);

        maybeApplyScoreboard(killer, kData);
        maybeApplyScoreboard(victim, vData);

        Bukkit.getLogger().info("[HeartClasses] " + killer.getName() + " stole a heart from " + victim.getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // First, allow the trade manager to handle the trade GUI.
        HeartTradeManager tradeManager = plugin.getTradeManager();
        if (tradeManager.onInventoryClick(e)) {
            return;
        }

        // Next, allow the TP Heart ability to handle its specialized inventory
        HeartAbility tpAbility = plugin.getAbilityManager().get(HeartType.TP);
        if (tpAbility instanceof me.kharique.heartclasses.abilities.TeleportAbility) {
            if (((me.kharique.heartclasses.abilities.TeleportAbility) tpAbility).handleInventoryClick(e)) {
                return;
            }
        }

        Inventory inv = e.getInventory();
        if (inv == null) return;

        String title = e.getView().getTitle();
        if (title == null) return;

        // Heart recipes list
        if (title.equals(ChatColor.RED + "Heart Recipes")) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;

            HeartType type = getTypeFromItem(clicked);
            if (type == null) return;

            new HeartRecipesGui(plugin).openRecipe((Player) e.getWhoClicked(), type, true);
        }

        // Recipe detail view
        if (title.startsWith(ChatColor.RED + "Recipe")) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;

            ItemMeta meta = clicked.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) return;

            String display = meta.getDisplayName();
            boolean isClassicView = title.contains("Classic");

            if (display.contains("Switch")) {
                HeartType type = getTypeFromItem(inv.getItem(24));
                if (type == null) return;
                new HeartRecipesGui(plugin).openRecipe((Player) e.getWhoClicked(), type, !isClassicView);
            }
        }
    }

    private void maybeApplyScoreboard(Player p, PlayerData data) {
        if (p.getWorld().getName().equalsIgnoreCase("heart")) {
            ScoreboardUtil.apply(p, data);
        }
    }

    private void refreshHeartFromInventory(Player p) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(p.getUniqueId());
        HeartType found = null;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) continue;
            if (!item.hasItemMeta()) continue;
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasLore()) continue;

            for (String line : meta.getLore()) {
                if (!line.contains("Type:")) continue;
                String[] parts = line.split(":" );
                if (parts.length < 2) continue;
                try {
                    found = HeartType.valueOf(parts[1].trim());
                    break;
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (found != null) break;
        }

        if (found != null && found != data.getHeartType()) {
            data.setHeartType(found);
            data.addOwnedType(found);
            plugin.getPlayerDataManager().tryUnlockGodHeart(p, data);
            maybeApplyScoreboard(p, data);
        }
    }

    private HeartType getTypeFromItem(ItemStack item) {
        if (item == null) return null;
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;

        for (String line : meta.getLore()) {
            if (line.contains("Type:")) {
                String[] parts = line.split(":");
                if (parts.length < 2) continue;
                String val = parts[1].trim();
                try {
                    return HeartType.valueOf(val);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return null;
    }
}
