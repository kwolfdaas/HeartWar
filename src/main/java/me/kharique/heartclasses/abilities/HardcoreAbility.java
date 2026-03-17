package me.kharique.heartclasses.abilities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HardcoreAbility implements HeartAbility {

    @Override
    public void onRightClick(Player player) { }

    @Override
    public void onKill(Player killer, Player victim) { }

    @Override
    public void onTick(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0, true, false));
    }
}
