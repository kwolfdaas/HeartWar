package me.kharique.heartclasses;

import me.kharique.heartclasses.abilities.AbilityManager;
import me.kharique.heartclasses.commands.HeartGuiCommand;
import me.kharique.heartclasses.commands.HeartHelpCommand;
import me.kharique.heartclasses.commands.HeartRecipesCommand;
import me.kharique.heartclasses.commands.HeartsCommand;
import me.kharique.heartclasses.data.PlayerDataManager;
import me.kharique.heartclasses.hearts.HeartManager;
import me.kharique.heartclasses.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class HeartClasses extends JavaPlugin {

    private static HeartClasses instance;
    private HeartManager heartManager;
    private PlayerDataManager playerDataManager;
    private AbilityManager abilityManager;
    private me.kharique.heartclasses.trade.HeartTradeManager tradeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.heartManager = new HeartManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.abilityManager = new AbilityManager(this);
        this.tradeManager = new me.kharique.heartclasses.trade.HeartTradeManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("heartrecipes").setExecutor(new HeartRecipesCommand(this));
        getCommand("heartgui").setExecutor(new HeartGuiCommand(this));
        getCommand("hearthelp").setExecutor(new HeartHelpCommand(this));
        getCommand("hearts").setExecutor(new HeartsCommand(this));
        getCommand("hearttrade").setExecutor(new me.kharique.heartclasses.commands.HeartTradeCommand(this));

        // Tick loop for abilities that need ticking (e.g., Hardcore)
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                me.kharique.heartclasses.data.PlayerData data = getPlayerDataManager().getOrCreate(p.getUniqueId());
                if (data == null) continue;
                me.kharique.heartclasses.abilities.HeartAbility ability = getAbilityManager().get(data.getHeartType());
                if (ability != null) ability.onTick(p);
            }
        }, 20L, 20L);

        setupHeartWorldBorder();
        Bukkit.getScheduler().runTaskTimer(this, this::updateHeartWorldBorder, 20L, 20L * 60 * 10); // every 10 minutes

        new AnnouncementManager(this).start();

        getLogger().info("HeartClasses enabled.");
    }

    @Override
    public void onDisable() {
        playerDataManager.saveAll();
        getLogger().info("HeartClasses disabled.");
    }

    private void setupHeartWorldBorder() {
        double size = getConfig().getDouble("heartWorld.borderSize", 5000.0);
        long last = getConfig().getLong("heartWorld.lastIncrease", 0L);

        World heartWorld = Bukkit.getWorld("heart");
        if (heartWorld == null) return;

        heartWorld.getWorldBorder().setSize(size);
        heartWorld.getWorldBorder().setCenter(heartWorld.getSpawnLocation());
        getConfig().set("heartWorld.borderSize", size);
        getConfig().set("heartWorld.lastIncrease", last);
        saveConfig();
    }

    private void updateHeartWorldBorder() {
        World heartWorld = Bukkit.getWorld("heart");
        if (heartWorld == null) return;

        double size = getConfig().getDouble("heartWorld.borderSize", 5000.0);
        long last = getConfig().getLong("heartWorld.lastIncrease", 0L);
        long now = System.currentTimeMillis();

        // Increase border every 7 days
        if (now - last >= 7L * 24 * 60 * 60 * 1000) {
            size += 500.0; // increase by 500 each week
            last = now;
            getConfig().set("heartWorld.borderSize", size);
            getConfig().set("heartWorld.lastIncrease", last);
            saveConfig();
        }

        heartWorld.getWorldBorder().setSize(size);
        heartWorld.getWorldBorder().setCenter(heartWorld.getSpawnLocation());
    }

    public static HeartClasses getInstance() {
        return instance;
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public me.kharique.heartclasses.trade.HeartTradeManager getTradeManager() {
        return tradeManager;
    }
}
