package me.kharique.heartclasses.utils;

import me.kharique.heartclasses.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardUtil {

    public static void apply(Player p, PlayerData data) {
        ScoreboardManager m = Bukkit.getScoreboardManager();
        if (m == null) return;

        Scoreboard board = m.getNewScoreboard();
        Objective obj = board.registerNewObjective("hearts", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        if (data.getHeartType() == me.kharique.heartclasses.hearts.HeartType.GOD) {
            obj.setDisplayName(ChatColor.GOLD + "GOD MODE");
        } else {
            obj.setDisplayName(ChatColor.RED + "Hearts");
        }

        obj.getScore(ChatColor.WHITE + "Type: " + ChatColor.RED + data.getHeartType().name()).setScore(3);
        obj.getScore(ChatColor.WHITE + "Hearts: " + ChatColor.RED + data.getHearts()).setScore(2);
        obj.getScore(ChatColor.DARK_GRAY + "world: heart").setScore(1);

        p.setScoreboard(board);
    }
}
