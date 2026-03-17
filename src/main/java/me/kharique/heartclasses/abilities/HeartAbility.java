package me.kharique.heartclasses.abilities;

import org.bukkit.entity.Player;

public interface HeartAbility {

    void onRightClick(Player player);

    void onKill(Player killer, Player victim);

    void onTick(Player player); // optional, can be empty
}
