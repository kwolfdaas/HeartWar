package me.kharique.heartclasses.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportAbility implements HeartAbility {

    private static final long COOLDOWN_MS = 3 * 60 * 1000;

    private final Map<UUID, Long> lastUse = new HashMap<>();
    private final Map<UUID, UUID> pendingSource = new HashMap<>();

    @Override
    public void onRightClick(Player player) {
        long now = System.currentTimeMillis();
        long last = lastUse.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < COOLDOWN_MS) {
            long remaining = (COOLDOWN_MS - (now - last)) / 1000;
            player.sendMessage(ChatColor.RED + "TP Heart is on cooldown. " + remaining + "s remaining.");
            return;
        }

        openSourceSelection(player);
    }

    @Override
    public void onKill(Player killer, Player victim) { }

    @Override
    public void onTick(Player player) { }

    private void openSourceSelection(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "TP Heart: Select source");
        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) break;
            inv.setItem(slot++, createPlayerSkull(online));
        }
        player.openInventory(inv);
    }

    private void openDestinationSelection(Player player, UUID source) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "TP Heart: Select destination");
        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) break;
            inv.setItem(slot++, createPlayerSkull(online));
        }
        pendingSource.put(player.getUniqueId(), source);
        player.openInventory(inv);
    }

    public boolean handleInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle() == null) return false;
        if (!e.getView().getTitle().startsWith(ChatColor.GOLD + "TP Heart:")) return false;

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return false;
        Player clicker = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.SKULL_ITEM) return true;

        SkullMeta meta = (SkullMeta) clicked.getItemMeta();
        if (meta == null || meta.getOwner() == null) return true;

        Player selected = Bukkit.getPlayer(meta.getOwner());
        if (selected == null) {
            clicker.sendMessage(ChatColor.RED + "That player is no longer online.");
            return true;
        }

        String title = e.getView().getTitle();
        if (title.contains("Select source")) {
            openDestinationSelection(clicker, selected.getUniqueId());
            return true;
        }

        if (title.contains("Select destination")) {
            UUID sourceId = pendingSource.remove(clicker.getUniqueId());
            if (sourceId == null) {
                clicker.sendMessage(ChatColor.RED + "Source not selected. Please try again.");
                return true;
            }
            Player source = Bukkit.getPlayer(sourceId);
            if (source == null) {
                clicker.sendMessage(ChatColor.RED + "Source player is no longer online.");
                return true;
            }

            source.teleport(selected.getLocation());
            clicker.sendMessage(ChatColor.GREEN + "Teleported " + source.getName() + " to " + selected.getName() + "!");
            lastUse.put(clicker.getUniqueId(), System.currentTimeMillis());
        }

        return true;
    }

    private ItemStack createPlayerSkull(Player target) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwner(target.getName());
            meta.setDisplayName(target.getName());
            skull.setItemMeta(meta);
        }
        return skull;
    }
}
