package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Shop implements InventoryHolder {

    private final UUID playerUUID;

    private Inventory invSeller = null;

    private Inventory invBuyer = null;

    private final String playerName;

    private SellingItem sponsor = null;

    private final int unlockedSlotsNumber = InternalUtil.CONFIGMANAGER.getUnlockedSlots();

    private ArrayList<SellingItem> items = new ArrayList<>();

    private ArrayList<String> offMessages = new ArrayList<>();

    private long lastSponsor = 0L;

    private final InventoryHolder inventoryHolder;

    public Shop(@NotNull UUID player, @NotNull String playerName, @NotNull ArrayList<String> offMessages, @Nullable SellingItem sponsor, @NotNull ArrayList<SellingItem> items, long lastSponsor) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(playerName);
        Objects.requireNonNull(offMessages);

        Objects.requireNonNull(items);

        InternalUtil.INVENTORYHOLDERS.add(this);
        this.playerUUID = player;
        this.playerName = playerName;
        this.offMessages = offMessages;
        this.sponsor = sponsor;
        this.items = items;
        this.lastSponsor = lastSponsor;
        inventoryHolder = this;
    }

    public Shop(@NotNull UUID player, @NotNull String playerName) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(playerName);
        InternalUtil.INVENTORYHOLDERS.add(this);

        this.playerUUID = player;
        this.playerName = playerName;
        inventoryHolder = this;

    }

    public boolean canSponsor(long time) {
        Player pl = Bukkit.getPlayer(playerUUID);
        if (pl != null && pl.hasPermission("shop.bypass.sponsortime")) {

            return true;
        }

        return (time - lastSponsor) / 60000 > ConfigManager.SPONSORTIME;

    }

    public long getLastSponsor() {
        return lastSponsor;
    }

    public long getMissTimeInMins(long time) {
        long i = ConfigManager.SPONSORTIME - (time - lastSponsor) / 60000;
        if (i < 0) {

            return 0;
        }

        return i;

    }

    private void applySponsor(long time) {

        lastSponsor = time;

    }

    public InventoryHolder getInventoryHolder() {
        return inventoryHolder;
    }

    public void openInventory(@NotNull Player player, ViewMode mode) {
        Objects.requireNonNull(player);

        if (mode == ViewMode.SELLER) {
            if (invSeller == null) {

                invSeller = getInventory();

            }

            player.openInventory(invSeller);

        } else {
            if (invBuyer == null) {

                invSeller = getInventory();

            }
            player.openInventory(invBuyer);

        }

    }

    public void updateInventory() {
        updateInvSeller();
        updateInvBuyer();

    }

    public SellingItem getItemAt(int slot) {

        for (SellingItem s : items) {
            if (s != null)
                if (s.getSlot() == slot)
                    return s;
        }
        return null;

    }

    @Override
    public @NotNull Inventory getInventory() {

        invSeller = Bukkit.createInventory(this, 18, InternalUtil.CONFIGMANAGER.getShopTitle(playerName));

        invBuyer = Bukkit.createInventory(this, 18, InternalUtil.CONFIGMANAGER.getShopTitle(playerName));

        updateInventory();

        return invSeller;
    }

    private void updateInvSeller() {
        if (invSeller != null) {

            ItemStack here = InternalUtil.UNLOCKEDSLOT;

            for (int i = 0; i < 18; i++) {

                invSeller.setItem(i, InternalUtil.LOCKEDSLOT);
            }

            for (int i = 1; i < unlockedSlotsNumber + 1; i++) {

                if (i > 7) {

                    invSeller.setItem(i + 2, here);

                } else {
                    invSeller.setItem(i, here);

                }

            }

            invSeller.setItem(0, InternalUtil.LOG);
            invSeller.setItem(8, InternalUtil.LOG);
            invSeller.setItem(9, InternalUtil.LOG);
            invSeller.setItem(17, InternalUtil.LOG);

            for (SellingItem s : items) {

                if (s.equals(sponsor)) {

                    invSeller.setItem(s.getSlot(), s.getWithPriceAndSponsorSeller());

                } else {

                    invSeller.setItem(s.getSlot(), s.getWithPriceSeller());

                }

            }

            ArrayList<LockedSlot> lo = RoadsideShops.INSTANCE.getCustomLockedSlots();

            Player p = Bukkit.getPlayer(playerUUID);
            if (p != null) {
                int y = 14;

                for (LockedSlot l : lo) {
                    if (l.isLocked(p)) {
                        if (y > 7) {

                            invSeller.setItem(y + 2, l.getItemStack());

                        } else {
                            invSeller.setItem(y, l.getItemStack());

                        }
                        y--;

                    }

                }

            }

        }

    }

    private void updateInvBuyer() {

        if (invBuyer != null) {
            ItemStack n = new ItemStack(Material.AIR);

            for (int i = 0; i < 18; i++) {

                invBuyer.setItem(i, n.clone());
            }

            invBuyer.setItem(0, InternalUtil.LOG);
            invBuyer.setItem(8, InternalUtil.LOG);
            invBuyer.setItem(9, InternalUtil.LOG);
            invBuyer.setItem(17, InternalUtil.LOG);

            for (SellingItem s : items) {

                if (s.equals(sponsor)) {

                    invBuyer.setItem(s.getSlot(), s.getWithPriceAndSponsorBuyer());

                } else {

                    invBuyer.setItem(s.getSlot(), s.getWithPriceBuyer());

                }

            }
        }

    }

    public void addItem(@NotNull SellingItem sellingItem) {
        addItem(sellingItem, false, false);

    }

    public void addItem(@NotNull SellingItem sellingItem, boolean isSponsoring, boolean sendMessage) {
        addItem(sellingItem, isSponsoring, sendMessage, null);

    }

    public void addItem(@NotNull SellingItem sellingItem, boolean isSponsoring, boolean notifyPlayer, @Nullable Player playerToNotify) {

        Objects.requireNonNull(sellingItem);

        if (playerToNotify == null && notifyPlayer) {
            playerToNotify = Bukkit.getPlayer(sellingItem.getPlayerUUID());

        }

        items.add(sellingItem);

        updateInventory();

        if (notifyPlayer && playerToNotify != null)
            playerToNotify.sendMessage(InternalUtil.CONFIGMANAGER.getPutItem(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

        if (isSponsoring) {
            if (notifyPlayer && playerToNotify != null)
                playerToNotify.sendMessage(InternalUtil.CONFIGMANAGER.getSponsorSet(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

            setSponsor(sellingItem);

        }
        applyChangesDB();
    }

    public void removeItem(int slot) {

        SellingItem i = getItemAt(slot);
        if (i != null) {

            removeItem(i, true, true, null);

        }

    }

    public void removeItem(@NotNull SellingItem sellingItem, @Nullable Player playerToNotify) {

        removeItem(sellingItem, true, true, playerToNotify);

    }

    public void removeItem(int slot, boolean notifyPlayer, boolean giveBack, @Nullable Player playerToNotify) {

        SellingItem i = getItemAt(slot);
        if (i != null) {

            removeItem(i, notifyPlayer, giveBack, playerToNotify);

        }

    }

    public void removeItem(@NotNull SellingItem sellingItem, boolean notifyPlayer, boolean giveBack, @Nullable Player playerToNotify) {

        Objects.requireNonNull(sellingItem);
        if (playerToNotify == null && notifyPlayer) {
            playerToNotify = Bukkit.getPlayer(sellingItem.getPlayerUUID());

        }

        if (items.remove(sellingItem)) {

            if (sellingItem.equals(sponsor)) {

                sponsor = null;
            }
            updateInventory();

            if (giveBack && playerToNotify != null) {

                HashMap<Integer, ItemStack> r = playerToNotify.getInventory().addItem(sellingItem.getItem());
                if (r.values().size() > 0) {
                    if (notifyPlayer) {

                        playerToNotify.sendMessage(InternalUtil.CONFIGMANAGER.getFullInvDrop());

                    }
                    for (ItemStack t : r.values()) {
                        playerToNotify.getWorld().dropItemNaturally(playerToNotify.getLocation(), t);

                    }
                } else {

                    if (notifyPlayer) {

                        playerToNotify.sendMessage(InternalUtil.CONFIGMANAGER.getRemoveItem(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

                    }

                }
            }

        }
        applyChangesDB();
    }

    public ItemStack generateMapItem(boolean isSponsoring, @Nullable SellingItem sellingItem) {

        ItemStack mapItem;

        if (sponsor != null && sponsor.equals(sellingItem)) {
            mapItem = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = mapItem.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());
            m.setLore(InternalUtil.CONFIGMANAGER.getSponsoredLore());
            mapItem.setItemMeta(m);
        } else if (canSponsor(System.currentTimeMillis())) {

            if (isSponsoring) {

                mapItem = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = mapItem.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

                if (sponsor != null) {
                    m.setLore(InternalUtil.CONFIGMANAGER.getSponsoringChange());
                } else {
                    m.setLore(InternalUtil.CONFIGMANAGER.getSponsoring());

                }
                mapItem.setItemMeta(m);

            } else {

                mapItem = new ItemStack(Material.PAPER);
                ItemMeta m = mapItem.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

                if (sponsor != null) {
                    m.setLore(InternalUtil.CONFIGMANAGER.getNotSponsoringChange());
                } else {
                    m.setLore(InternalUtil.CONFIGMANAGER.getNotSponsoring());

                }
                mapItem.setItemMeta(m);

            }

        } else {
            mapItem = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = mapItem.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

            m.setLore(InternalUtil.CONFIGMANAGER.getWaitToSponsor(getMissTimeInMins(System.currentTimeMillis())));

            mapItem.setItemMeta(m);

        }
        return mapItem.clone();
    }

    public void setSponsor(@NotNull SellingItem sellingItem) {
        Objects.requireNonNull(sellingItem);

        applySponsor(System.currentTimeMillis());
        sponsor = sellingItem;
        updateInventory();
        applyChangesDB();

    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void emptyItems() {

        items.clear();
        applyChangesDB();
    }

    public SellingItem getSponsor() {
        return sponsor;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getUnlockedSlotsNumber() {
        return unlockedSlotsNumber;
    }

    public ArrayList<SellingItem> getItems() {
        return new ArrayList<>(items);
    }

    public ArrayList<String> getOffMessages() {
        return new ArrayList<>(offMessages);
    }

    public void addMessage(@NotNull String s) {
        Objects.requireNonNull(s);

        offMessages.add(s);
        applyChangesDB();
    }

    public void clearMessages() {
        offMessages.clear();
    }

    public void applyChangesDB() {

        CompletableFuture.runAsync(() -> RoadsideShops.saveShop(this));

    }

}
