package me.kharique.heartclasses;

import org.bukkit.Bukkit;

import java.util.List;

public class AnnouncementManager {

    private final HeartClasses plugin;
    private int taskId = -1;
    private int index = 0;

    private static final List<String> MESSAGES = List.of(
            "\u00A73[HeartClasses] \u00A7eTip: Use /hearthelp to receive a help book.",
            "\u00A73[HeartClasses] \u00A7eTip: Use /heartrecipes to view available heart recipes.",
            "\u00A73[HeartClasses] \u00A7eTip: Hearts are earned by playing in the 'heart' world and defeating opponents.",
            "\u00A73[HeartClasses] \u00A7eTip: Collect all heart types to unlock the GOD HEART.",
            "\u00A73[HeartClasses] \u00A7eTip: The TP Heart can move a player to another (3min cooldown)."
    );

    public AnnouncementManager(HeartClasses plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            String msg = MESSAGES.get(index);
            Bukkit.getServer().broadcastMessage(msg);
            index = (index + 1) % MESSAGES.size();
        }, 20L, 20L * 120).getTaskId();
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
