package me.kharique.heartclasses.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BorderAbility implements HeartAbility {

    @Override
    public void onRightClick(Player player) {
        // Create a temporary bedrock box around the player for 10 seconds
        // (instead of moving the global world border)
        Location loc = player.getLocation();
        int px = loc.getBlockX();
        int py = loc.getBlockY();
        int pz = loc.getBlockZ();

        // Store original block states so we can restore them later
        List<BlockState> originalStates = new ArrayList<>();

        int radius = 1;
        int minY = py - 1;
        int maxY = py + 2;

        for (int x = px - radius; x <= px + radius; x++) {
            for (int z = pz - radius; z <= pz + radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    boolean isBorder = x == px - radius || x == px + radius || z == pz - radius || z == pz + radius || y == minY || y == maxY;
                    if (!isBorder) {
                        continue;
                    }

                    Block block = player.getWorld().getBlockAt(x, y, z);
                    originalStates.add(block.getState());
                    block.setType(Material.BEDROCK, false);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(org.bukkit.plugin.java.JavaPlugin.getPlugin(me.kharique.heartclasses.HeartClasses.class), () -> {
            for (BlockState state : originalStates) {
                state.update(true, false);
            }
        }, 200L);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0, true, false));
        player.sendMessage("\u00A7aBorder Medallion activated for 10 seconds!");
    }

    @Override
    public void onKill(Player killer, Player victim) { }

    @Override
    public void onTick(Player player) { }
}
