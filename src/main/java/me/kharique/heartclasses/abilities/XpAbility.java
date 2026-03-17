package me.kharique.heartclasses.abilities;

import org.bukkit.entity.Player;

public class XpAbility implements HeartAbility {

    @Override
    public void onRightClick(Player player) { }

    @Override
    public void onKill(Player killer, Player victim) {
        killer.giveExp(10);
        killer.sendMessage("\u00A7aYou gained extra XP from your XP Heart!");
    }

    @Override
    public void onTick(Player player) { }
}
