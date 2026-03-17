package me.kharique.heartclasses.data;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.hearts.HeartDefinition;
import me.kharique.heartclasses.hearts.HeartManager;
import me.kharique.heartclasses.hearts.HeartType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {

    private final HeartClasses plugin;
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerDataManager(HeartClasses plugin) {
        this.plugin = plugin;
        loadAll();
    }

    public PlayerData getOrCreate(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, id -> {
            HeartManager hm = plugin.getHeartManager();
            HeartDefinition def = hm.getRandomHeart();
            return new PlayerData(id, def.getType(), 1);
        });
    }

    public void loadAll() {
        FileConfiguration cfg = plugin.getConfig();
        if (!cfg.isConfigurationSection("players")) return;

        for (String key : cfg.getConfigurationSection("players").getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String typeName = cfg.getString("players." + key + ".type", HeartType.BASEFINDER.name());
            int hearts = cfg.getInt("players." + key + ".hearts", 1);
            HeartType type = HeartType.valueOf(typeName);

            // owned heart types (for GOD unlock)
            Set<HeartType> owned = new HashSet<>();
            for (String s : cfg.getStringList("players." + key + ".owned")) {
                try {
                    owned.add(HeartType.valueOf(s));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (owned.isEmpty()) {
                owned.add(type);
            }

            dataMap.put(uuid, new PlayerData(uuid, type, hearts, owned));
        }
    }

    public void saveAll() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("players", null);
        for (PlayerData data : dataMap.values()) {
            String base = "players." + data.getUuid().toString();
            cfg.set(base + ".type", data.getHeartType().name());
            cfg.set(base + ".hearts", data.getHearts());
            cfg.set(base + ".owned", new java.util.ArrayList<>(
                    data.getOwnedTypes().stream().map(Enum::name).collect(java.util.stream.Collectors.toList())
            ));
        }
        plugin.saveConfig();
    }

    public boolean hasAllHearts(PlayerData data) {
        for (HeartType type : HeartType.values()) {
            if (type == HeartType.GOD) continue;
            if (!data.hasHeart(type)) return false;
        }
        return true;
    }

    public void tryUnlockGodHeart(org.bukkit.entity.Player player, PlayerData data) {
        if (data.getHeartType() == HeartType.GOD) return;
        if (!hasAllHearts(data)) return;

        data.setHeartType(HeartType.GOD);
        player.getInventory().addItem(plugin.getHeartManager().createHeartItem(
                plugin.getHeartManager().getDefinition(HeartType.GOD)
        ));
        player.getInventory().addItem(me.kharique.heartclasses.utils.GodWeapon.create());
        player.sendMessage(org.bukkit.ChatColor.GOLD + "" + org.bukkit.ChatColor.BOLD + "You have unlocked the GOD HEART!");
    }
}

