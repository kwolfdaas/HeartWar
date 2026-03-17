package me.kharique.heartclasses.utils;

import me.kharique.heartclasses.hearts.HeartType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class HeartHelpBook {

    public static ItemStack create(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return book;

        meta.setTitle("Heart Help");
        meta.setAuthor("HeartClasses");

        StringBuilder page = new StringBuilder();
        page.append(ChatColor.GOLD).append("HeartClasses Help\n");
        page.append(ChatColor.GRAY).append("Use /hearthelp to get this book anytime.\n\n");

        page.append(ChatColor.AQUA).append("Commands:\n");
        page.append(ChatColor.WHITE).append("/hearts\n");
        page.append(ChatColor.WHITE).append("/heartrecipes\n");
        page.append(ChatColor.WHITE).append("/heartgui (admin)\n");
        page.append(ChatColor.WHITE).append("/hearthelp\n\n");

        page.append(ChatColor.AQUA).append("How it works:\n");
        page.append(ChatColor.WHITE).append("Collect hearts by playing and killing in the heart world.\n");
        page.append(ChatColor.WHITE).append("Each heart type grants a special ability.\n");
        page.append(ChatColor.WHITE).append("Collect all heart types to unlock the GOD HEART.\n\n");

        page.append(ChatColor.AQUA).append("Heart Types:\n");
        for (me.kharique.heartclasses.hearts.HeartDefinition def : me.kharique.heartclasses.HeartClasses.getInstance().getHeartManager().getAll()) {
            page.append(ChatColor.GREEN).append("- ").append(def.getDisplayName()).append("\n");
            page.append(ChatColor.WHITE).append("  ").append(def.getDescription()).append("\n");
        }

        meta.addPage(page.toString());
        book.setItemMeta(meta);
        return book;
    }
}
