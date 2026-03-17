package me.kharique.heartclasses.abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class LifestealAbility implements HeartAbility {

    @Override
    public void onRightClick(Player player) { }

    @Override
    public void onKill(Player killer, Player victim) {
        try {
            double max = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Math.min(max + 2.0, 40.0));
        } catch (Throwable ignored) {
            // Attribute API might not exist; ignore and just heal.
        }
        killer.setHealth(Math.min(killer.getHealth() + 4.0, killer.getMaxHealth()));
    }

    @Override
    public void onTick(Player player) { }
}
