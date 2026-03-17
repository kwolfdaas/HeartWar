package me.kharique.heartclasses.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BasefinderAbility implements HeartAbility {

    private static final long SPECTATOR_DURATION_TICKS = 20L * 10; // 10 seconds

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerState> active = new HashMap<>();

    public BasefinderAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onRightClick(Player player) {
        UUID id = player.getUniqueId();
        if (active.containsKey(id)) {
            player.sendMessage("\u00A7eBasefinder: You are already in spectator mode.");
            return;
        }

        PlayerState state = new PlayerState(player.getGameMode(), player.getLocation());
        active.put(id, state);

        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("\u00A7eBasefinder: Spectator mode enabled for 10 seconds.");

        Bukkit.getScheduler().runTaskLater(plugin, () -> restore(player), SPECTATOR_DURATION_TICKS);

        // Show chest highlights while in spectator mode
        highlightChests(player);
    }

    private void highlightChests(Player player) {
        int radius = 20;
        Location loc = player.getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (b.getType() == Material.CHEST) {
                        player.playEffect(b.getLocation().add(0.5, 1.2, 0.5), Effect.HEART, 0);
                    }
                }
            }
        }
    }

    private void restore(Player player) {
        UUID id = player.getUniqueId();
        PlayerState state = active.remove(id);
        if (state == null) return;

        player.teleport(state.location);
        player.setGameMode(state.gameMode);
        player.sendMessage("\u00A7aBasefinder: Returned to normal mode.");
    }

    @Override
    public void onKill(Player killer, Player victim) { }

    @Override
    public void onTick(Player player) { }

    private static class PlayerState {
        final GameMode gameMode;
        final Location location;

        PlayerState(GameMode gameMode, Location location) {
            this.gameMode = gameMode;
            this.location = location;
        }
    }
}
