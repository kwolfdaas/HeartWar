package me.kharique.heartclasses.abilities;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.hearts.HeartType;

import java.util.EnumMap;
import java.util.Map;

public class AbilityManager {

    private final Map<HeartType, HeartAbility> map = new EnumMap<>(HeartType.class);

    public AbilityManager(HeartClasses plugin) {
        map.put(HeartType.BASEFINDER, new BasefinderAbility(plugin));
        map.put(HeartType.LIFESTEAL, new LifestealAbility());
        map.put(HeartType.HARDCORE, new HardcoreAbility());
        map.put(HeartType.TP, new TeleportAbility());
        map.put(HeartType.XP, new XpAbility());
        map.put(HeartType.BORDER, new BorderAbility());
        map.put(HeartType.GOD, new GodAbility());
    }

    public HeartAbility get(HeartType type) {
        return map.get(type);
    }
}
