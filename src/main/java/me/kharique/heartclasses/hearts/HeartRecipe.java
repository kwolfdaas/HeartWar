package me.kharique.heartclasses.hearts;

import org.bukkit.Material;

public class HeartRecipe {

    private final HeartType type;
    private final Material[][] grid; // 3x3

    public HeartRecipe(HeartType type, Material[][] grid) {
        this.type = type;
        this.grid = grid;
    }

    public HeartType getType() {
        return type;
    }

    public Material[][] getGrid() {
        return grid;
    }
}
