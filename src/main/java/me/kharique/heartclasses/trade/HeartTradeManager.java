package me.kharique.heartclasses.trade;

import me.kharique.heartclasses.HeartClasses;
import me.kharique.heartclasses.data.PlayerData;
import me.kharique.heartclasses.data.PlayerDataManager;
import me.kharique.heartclasses.hearts.HeartManager;
import me.kharique.heartclasses.hearts.HeartType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HeartTradeManager {

    private final HeartClasses plugin;
    private final Map<UUID, TradeSession> activeTrades = new HashMap<>();

    public HeartTradeManager(HeartClasses plugin) {
        this.plugin = plugin;
    }

    public boolean isTrading(Player player) {
        return activeTrades.containsKey(player.getUniqueId());
    }

    public void requestTrade(Player requester, Player target) {
        if (requester.equals(target)) {
            requester.sendMessage(ChatColor.RED + "You cannot trade with yourself.");
            return;
        }
        if (isTrading(requester) || isTrading(target)) {
            requester.sendMessage(ChatColor.RED + "Either you or that player is already in a trade.");
            return;
        }

        TradeSession session = new TradeSession(requester, target);
        activeTrades.put(requester.getUniqueId(), session);
        activeTrades.put(target.getUniqueId(), session);

        requester.sendMessage(ChatColor.GREEN + "Trade request sent to " + target.getName() + ".");
        target.sendMessage(ChatColor.AQUA + requester.getName() + " wants to trade hearts with you. Open the trade window to accept.");

        openTradeInventories(session);
    }

    private void openTradeInventories(TradeSession session) {
        session.openFor(session.requester);
        session.openFor(session.target);
    }

    public boolean onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof TradeInventoryHolder)) return false;

        TradeInventoryHolder tradeHolder = (TradeInventoryHolder) holder;
        TradeSession session = tradeHolder.getSession();
        if (session == null) return false;

        event.setCancelled(true);
        Player clicker = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot == 8) {
            session.toggleAccept(clicker);
            if (session.isAcceptedByBoth()) {
                completeTrade(session);
            } else {
                session.updateInventories();
            }
        }
        return true;
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof TradeInventoryHolder)) return;

        TradeInventoryHolder tradeHolder = (TradeInventoryHolder) holder;
        TradeSession session = tradeHolder.getSession();
        if (session == null) return;

        // If the trade already completed, ignore the close event
        if (!activeTrades.containsKey(session.requester.getUniqueId())) return;

        // Only cancel when either player closes their own trade window.
        if (!session.isParticipant((Player) event.getPlayer())) return;

        activeTrades.remove(session.requester.getUniqueId());
        activeTrades.remove(session.target.getUniqueId());
        session.cancel("Trade cancelled (window closed).");
    }

    private void completeTrade(TradeSession session) {
        PlayerDataManager pdm = plugin.getPlayerDataManager();
        PlayerData dataA = pdm.getOrCreate(session.requester.getUniqueId());
        PlayerData dataB = pdm.getOrCreate(session.target.getUniqueId());

        HeartType typeA = dataA.getHeartType();
        HeartType typeB = dataB.getHeartType();

        dataA.setHeartType(typeB);
        dataB.setHeartType(typeA);
        pdm.saveAll();

        HeartManager hm = plugin.getHeartManager();
        session.requester.getInventory().addItem(hm.createHeartItem(hm.getDefinition(dataA.getHeartType())));
        session.target.getInventory().addItem(hm.createHeartItem(hm.getDefinition(dataB.getHeartType())));

        session.requester.sendMessage(ChatColor.GREEN + "Trade complete! You now have " + dataA.getHeartType().name() + " heart.");
        session.target.sendMessage(ChatColor.GREEN + "Trade complete! You now have " + dataB.getHeartType().name() + " heart.");

        session.close();
        activeTrades.remove(session.requester.getUniqueId());
        activeTrades.remove(session.target.getUniqueId());
    }

    private void cancelTrade(TradeSession session, String reason) {
        session.cancel(reason);
        activeTrades.remove(session.requester.getUniqueId());
        activeTrades.remove(session.target.getUniqueId());
    }

    private class TradeSession {
        private final Player requester;
        private final Player target;
        private final Set<UUID> accepted = new HashSet<>();

        private Inventory requesterInv;
        private Inventory targetInv;

        TradeSession(Player requester, Player target) {
            this.requester = requester;
            this.target = target;
        }

        void openFor(Player player) {
            String title = ChatColor.DARK_RED + "Heart Trade: " + (player == requester ? target.getName() : requester.getName());
            TradeInventoryHolder holder = new TradeInventoryHolder(this, player);
            Inventory inv = Bukkit.createInventory(holder, 9, title);
            holder.setInventory(inv);

            if (player == requester) {
                requesterInv = inv;
            } else {
                targetInv = inv;
            }
            updateInventory(player, inv);
            player.openInventory(inv);
        }

        void updateInventory(Player player, Inventory inv) {
            PlayerDataManager pdm = plugin.getPlayerDataManager();
            PlayerData ownData = pdm.getOrCreate(player.getUniqueId());
            PlayerData otherData = pdm.getOrCreate(player == requester ? target.getUniqueId() : requester.getUniqueId());

            HeartManager hm = plugin.getHeartManager();

            // Slot 0: own heart
            inv.setItem(0, hm.createHeartItem(hm.getDefinition(ownData.getHeartType())));
            // Slot 2: other player's heart
            inv.setItem(2, hm.createHeartItem(hm.getDefinition(otherData.getHeartType())));

            // Slot 4: placeholder
            inv.setItem(4, createGlassPane("-- Heart Trade --"));

            // Slot 8: accept button
            boolean isAccepted = accepted.contains(player.getUniqueId());
            inv.setItem(8, createAcceptButton(isAccepted));
        }

        void updateInventories() {
            if (requesterInv != null) updateInventory(requester, requesterInv);
            if (targetInv != null) updateInventory(target, targetInv);
        }

        void toggleAccept(Player player) {
            UUID id = player.getUniqueId();
            if (accepted.contains(id)) {
                accepted.remove(id);
            } else {
                accepted.add(id);
            }
        }

        boolean isAcceptedByBoth() {
            return accepted.contains(requester.getUniqueId()) && accepted.contains(target.getUniqueId());
        }

        boolean isParticipant(Player player) {
            return player.equals(requester) || player.equals(target);
        }

        void close() {
            requester.closeInventory();
            target.closeInventory();
        }

        void cancel(String reason) {
            requester.sendMessage(ChatColor.RED + reason);
            target.sendMessage(ChatColor.RED + reason);
            close();
        }

        private ItemStack createGlassPane(String name) {
            ItemStack glass = new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(name);
            glass.setItemMeta(meta);
            return glass;
        }

        private ItemStack createAcceptButton(boolean accepted) {
            org.bukkit.Material mat = accepted ? org.bukkit.Material.WOOL : org.bukkit.Material.WOOL;
            short data = (short) (accepted ? 13 : 14); // green/red wool
            ItemStack button = new ItemStack(mat, 1, data);
            ItemMeta meta = button.getItemMeta();
            meta.setDisplayName(accepted ? ChatColor.GREEN + "Accepted" : ChatColor.YELLOW + "Click to Accept");
            button.setItemMeta(meta);
            return button;
        }
    }

    public static class TradeInventoryHolder implements InventoryHolder {
        private final TradeSession session;
        private final Player owner;
        private Inventory inventory;

        public TradeInventoryHolder(TradeSession session, Player owner) {
            this.session = session;
            this.owner = owner;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public TradeSession getSession() {
            return session;
        }

        public Player getOwner() {
            return owner;
        }
    }

    public static class InventoryClickEventWrapper {
        private final org.bukkit.event.inventory.InventoryClickEvent event;
        private final TradeSession session;
        private final Player clicker;
        private final int slot;

        public InventoryClickEventWrapper(org.bukkit.event.inventory.InventoryClickEvent event, TradeSession session, Player clicker, int slot) {
            this.event = event;
            this.session = session;
            this.clicker = clicker;
            this.slot = slot;
        }

        public org.bukkit.event.inventory.InventoryClickEvent getEvent() {
            return event;
        }

        public TradeSession getSession() {
            return session;
        }

        public Player getClicker() {
            return clicker;
        }

        public int getSlot() {
            return slot;
        }
    }
}
