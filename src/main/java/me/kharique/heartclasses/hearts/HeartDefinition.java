package me.kharique.heartclasses.hearts;

import org.bukkit.Material;

public class HeartDefinition {

    private final HeartType type;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final int tier;

    public HeartDefinition(HeartType type, String displayName, String description, Material icon, int tier) {
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.tier = tier;
    }

    public HeartType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public int getTier() {
        return tier;
    }
}
