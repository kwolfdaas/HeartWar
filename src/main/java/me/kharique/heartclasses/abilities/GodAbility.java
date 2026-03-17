package me.kharique.heartclasses.abilities;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GodAbility implements HeartAbility {

    @Override
    public void onRightClick(Player player) {
        // Basefinder effect
        int radius = 25;
        Location loc = player.getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = loc.getWorld().getBlockAt(
                            loc.getBlockX() + x,
                            loc.getBlockY() + y,
                            loc.getBlockZ() + z
                    );
                    if (b.getType() == Material.CHEST) {
                        player.playEffect(b.getLocation().add(0.5, 1.2, 0.5), Effect.HEART, 0);
                    }
                }
            }
        }
    }

    @Override
    public void onKill(Player killer, Player victim) {
        // Lifesteal effect
        killer.setHealth(Math.min(killer.getHealth() + 6.0, killer.getMaxHealth()));
    }

    @Override
    public void onTick(Player player) {
        // Hardcore + Speed + Strength
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false));
    }
}
