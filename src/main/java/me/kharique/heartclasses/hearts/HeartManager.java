package me.kharique.heartclasses.hearts;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.hearts.HeartRecipe;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HeartManager {

    private final HeartClasses plugin;
    private final Map<HeartType, HeartDefinition> hearts = new EnumMap<>(HeartType.class);
    private final Map<HeartType, HeartRecipe> recipes = new EnumMap<>(HeartType.class);
    private final Random random = new Random();

    public HeartManager(HeartClasses plugin) {
        this.plugin = plugin;
        registerDefaults();
    }

    private void registerDefaults() {
        // You can later load these from config
        register(new HeartDefinition(
                HeartType.BASEFINDER,
                ChatColor.RED + "Basefinder Heart",
                ChatColor.GRAY + "Reveals nearby bases.",
                Material.APPLE,
                1
        ));
        register(new HeartDefinition(
                HeartType.LIFESTEAL,
                ChatColor.DARK_RED + "Lifesteal Heart",
                ChatColor.GRAY + "Steal extra hearts on kill.",
                Material.APPLE,
                1
        ));
        register(new HeartDefinition(
                HeartType.HARDCORE,
                ChatColor.DARK_PURPLE + "Hardcore Heart",
                ChatColor.GRAY + "Massive buffs, but risky.",
                Material.APPLE,
                1
        ));
        register(new HeartDefinition(
                HeartType.TP,
                ChatColor.AQUA + "Teleport Heart",
                ChatColor.GRAY + "Pick two players and teleport one to the other (3 min cooldown).",
                Material.ENDER_PEARL,
                1
        ));
        register(new HeartDefinition(
                HeartType.XP,
                ChatColor.GREEN + "XP Heart",
                ChatColor.GRAY + "Gain bonus XP on kill.",
                Material.EXP_BOTTLE,
                1
        ));
        register(new HeartDefinition(
                HeartType.BORDER,
                ChatColor.DARK_GREEN + "Border Medallion",
                ChatColor.GRAY + "Create a temporary border around you.",
                Material.EYE_OF_ENDER,
                1
        ));

        // Recipes (classic 3x3 grid)
        recipes.put(HeartType.BASEFINDER, new HeartRecipe(
                HeartType.BASEFINDER,
                new Material[][]{
                        {Material.COMPASS, Material.REDSTONE, Material.IRON_INGOT},
                        {Material.AIR, Material.LAPIS_BLOCK, Material.AIR},
                        {Material.IRON_INGOT, Material.REDSTONE, Material.COMPASS}
                }
        ));
        recipes.put(HeartType.LIFESTEAL, new HeartRecipe(
                HeartType.LIFESTEAL,
                new Material[][]{
                        {Material.GOLD_BLOCK, Material.DIAMOND, Material.GOLD_BLOCK},
                        {Material.AIR, Material.REDSTONE_BLOCK, Material.AIR},
                        {Material.GOLD_INGOT, Material.GOLD_INGOT, Material.GOLD_INGOT}
                }
        ));
        recipes.put(HeartType.HARDCORE, new HeartRecipe(
                HeartType.HARDCORE,
                new Material[][]{
                        {Material.OBSIDIAN, Material.DIAMOND, Material.OBSIDIAN},
                        {Material.OBSIDIAN, Material.BLAZE_POWDER, Material.OBSIDIAN},
                        {Material.OBSIDIAN, Material.OBSIDIAN, Material.OBSIDIAN}
                }
        ));
        recipes.put(HeartType.TP, new HeartRecipe(
                HeartType.TP,
                new Material[][]{
                        {Material.ENDER_PEARL, Material.EYE_OF_ENDER, Material.ENDER_PEARL},
                        {Material.AIR, Material.DIAMOND, Material.AIR},
                        {Material.ENDER_PEARL, Material.EYE_OF_ENDER, Material.ENDER_PEARL}
                }
        ));
        recipes.put(HeartType.XP, new HeartRecipe(
                HeartType.XP,
                new Material[][]{
                        {Material.EXP_BOTTLE, Material.EXP_BOTTLE, Material.EXP_BOTTLE},
                        {Material.AIR, Material.GOLD_BLOCK, Material.AIR},
                        {Material.EXP_BOTTLE, Material.EXP_BOTTLE, Material.EXP_BOTTLE}
                }
        ));
        recipes.put(HeartType.BORDER, new HeartRecipe(
                HeartType.BORDER,
                new Material[][]{
                        {Material.EYE_OF_ENDER, Material.OBSIDIAN, Material.EYE_OF_ENDER},
                        {Material.AIR, Material.DIAMOND, Material.AIR},
                        {Material.EYE_OF_ENDER, Material.OBSIDIAN, Material.EYE_OF_ENDER}
                }
        ));

        // GOD Heart (requires collecting all other hearts)
        register(new HeartDefinition(
                HeartType.GOD,
                org.bukkit.ChatColor.GOLD + "" + org.bukkit.ChatColor.BOLD + "GOD HEART",
                org.bukkit.ChatColor.YELLOW + "The ultimate heart. Grants ALL abilities and an OP weapon.",
                Material.NETHER_STAR,
                1
        ));
        recipes.put(HeartType.GOD, new HeartRecipe(
                HeartType.GOD,
                new Material[][]{
                        {Material.AIR, Material.AIR, Material.AIR},
                        {Material.AIR, Material.AIR, Material.AIR},
                        {Material.AIR, Material.AIR, Material.AIR}
                }
        ));
        // add more hearts / recipes here
    }

    public void register(HeartDefinition def) {
        hearts.put(def.getType(), def);
    }

    public HeartDefinition getDefinition(HeartType type) {
        return hearts.get(type);
    }

    public HeartRecipe getRecipe(HeartType type) {
        return recipes.get(type);
    }

    public Collection<HeartDefinition> getAll() {
        return hearts.values();
    }

    public HeartDefinition getRandomHeart() {
        List<HeartDefinition> list = new ArrayList<>();
        for (HeartDefinition def : hearts.values()) {
            if (def.getType() == HeartType.GOD) continue;
            list.add(def);
        }
        if (list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    public ItemStack createHeartItem(HeartDefinition def) {
        ItemStack item = new ItemStack(def.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(def.getDisplayName());
        meta.setLore(Arrays.asList(
                ChatColor.DARK_GRAY + "Type: " + def.getType().name(),
                ChatColor.DARK_GRAY + "Tier: " + def.getTier(),
                ChatColor.GRAY + def.getDescription()
        ));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
