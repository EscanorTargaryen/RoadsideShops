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

/**
 * Class representing a shop.
 */
public class Shop implements InventoryHolder {

    /**
     * The shop UUID owner.
     */
    private final UUID playerUUID;

    /**
     * The inventory that is shown to the seller.
     */
    private Inventory invSeller = null;

    /**
     * The inventory that is shown to the buyer.
     */
    private Inventory invBuyer = null;

    /**
     * The name of the shop owner.
     */
    private final String playerName;

    /**
     * The item that is sponsored.
     * If it is null it means that you are not sponsoring any item.
     */
    private SellingItem sponsor = null;

    /**
     * Number of slots unlocked by default.
     */
    private final int unlockedSlotsNumber = InternalUtil.CONFIGMANAGER.getUnlockedSlots();

    /**
     * The items for sale.
     */
    private HashMap<Integer, SellingItem> items = new HashMap<>();

    /**
     * If someone buys in your shop while you are offline, the sale messages will be recorded and then shown when the seller returns.
     */
    private ArrayList<String> offMessages = new ArrayList<>();

    /**
     * The last time an item was sponsored.
     */
    private long lastSponsor = 0L;

    /**
     * The inventory holder of the inventory of the shop.
     */
    private final InventoryHolder inventoryHolder;

    /**
     * Creates a new Shop.
     *
     * @param player      The UUID of the owner.
     * @param playerName  The name of the owner.
     * @param offMessages Sales messages recorded while the owner was offline.
     * @param sponsor     The sponsored item.
     * @param items       The items for sale.
     * @param lastSponsor The last time an item was sponsored.
     */
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

        for (SellingItem i : items) {
            this.items.put(i.getSlot(), i);

        }

        this.lastSponsor = lastSponsor;
        inventoryHolder = this;
    }

    /**
     * Creates a new Shop.
     *
     * @param player     The UUID of the owner.
     * @param playerName The name of the owner.
     */
    public Shop(@NotNull UUID player, @NotNull String playerName) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(playerName);
        InternalUtil.INVENTORYHOLDERS.add(this);

        this.playerUUID = player;
        this.playerName = playerName;
        inventoryHolder = this;

    }

    /**
     * Returns if the player can sponsor a new item.
     *
     * @return if the player can sponsor a new item.
     */
    public boolean canSponsor() {
        long time = System.currentTimeMillis();
        Player pl = Bukkit.getPlayer(playerUUID);
        if (pl != null && pl.hasPermission("shop.bypass.sponsortime")) {

            return true;
        }

        return (time - lastSponsor) / 60000 > ConfigManager.SPONSORTIME;

    }

    public long getLastSponsor() {
        return lastSponsor;
    }

    /**
     * Return how many minutes until the next sponsorship.
     *
     * @return how many minutes until the next sponsorship.
     */
    public long getMissTimeInMins() {
        long time = System.currentTimeMillis();
        long i = (ConfigManager.SPONSORTIME * 1000 - (time - lastSponsor)) / 60000;
        if (i < 0) {

            return 0;
        }

        return i;

    }

    /**
     * Set to now the last sponsorship
     */
    private void applySponsor() {

        lastSponsor = System.currentTimeMillis();

    }

    public InventoryHolder getInventoryHolder() {
        return inventoryHolder;
    }

    /**
     * Opens the shop inventory to the player.
     *
     * @param player A player to show the shop.
     * @param mode   Whether it is a seller or a buyer who displays it.
     */
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

    /**
     * Update inventories in real time.
     */
    public void updateInventory() {
        updateInvSeller();
        updateInvBuyer();

    }

    /**
     * Returns the items located at the specified slot.
     * (Can be null)
     *
     * @param slot A slot.
     * @return the items located at the specified slot.
     */
    @Nullable
    public SellingItem getItemAt(int slot) {

        return items.get(slot);

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

            for (SellingItem s : getItems()) {

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
                    if (l.isLocked(p) && y > 0) {
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

            for (SellingItem s : getItems()) {

                if (s.equals(sponsor)) {

                    invBuyer.setItem(s.getSlot(), s.getWithPriceAndSponsorBuyer());

                } else {

                    invBuyer.setItem(s.getSlot(), s.getWithPriceBuyer());

                }

            }
        }

    }

    /**
     * Add item for sale in the shop. (The player will not be notified of this addition)
     *
     * @param sellingItem  The item for sale.
     * @param isSponsoring If it is an item in sponsorship.
     */
    public void addItem(@NotNull SellingItem sellingItem, boolean isSponsoring) {
        addItem(sellingItem, isSponsoring, false);

    }

    /**
     * Add item for sale in the shop. (If it has been set to notify the player, this will be done only if he is online.)
     *
     * @param sellingItem  The item for sale.
     * @param isSponsoring If it is an item in sponsorship.
     * @param sendMessage  Whether to notify the player that this addition has occurred.
     */
    public void addItem(@NotNull SellingItem sellingItem, boolean isSponsoring, boolean sendMessage) {
        addItem(sellingItem, isSponsoring, sendMessage, null);

    }

    /**
     * Add item for sale in the shop.
     *
     * @param sellingItem    The item for sale.
     * @param isSponsoring   If it is an item in sponsorship.
     * @param notifyPlayer   Whether to notify the player that this addition has occurred.
     * @param playerToNotify Which player to notify for this addition.
     */
    public void addItem(@NotNull SellingItem sellingItem, boolean isSponsoring, boolean notifyPlayer, @Nullable Player playerToNotify) {

        Objects.requireNonNull(sellingItem);

        if (playerToNotify == null && notifyPlayer) {
            playerToNotify = Bukkit.getPlayer(sellingItem.getPlayerUUID());

        }

        items.put(sellingItem.getSlot(), sellingItem);

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

    /**
     * Remove item for sale. (The player will be notified and receive the item back if it is online)
     * If the player is not online and needs to receive the item back, it will be lost.
     *
     * @param slot The slot of the item for sale.
     */
    public void removeItem(int slot) {

        SellingItem i = getItemAt(slot);
        if (i != null) {

            removeItem(i, true, true, null);

        }

    }

    /**
     * Remove item for sale. (The player will be notified and receive the item back)
     * If the player is not online and needs to receive the item back, it will be lost.
     *
     * @param sellingItem    The item for sale.
     * @param playerToNotify Which player to notify for this removal.
     */
    public void removeItem(@NotNull SellingItem sellingItem, @Nullable Player playerToNotify) {

        removeItem(sellingItem, true, true, playerToNotify);

    }

    /**
     * Remove item for sale.
     * If the player is not online and needs to receive the item back, it will be lost.
     *
     * @param slot           The slot of the item for sale.
     * @param notifyPlayer   Whether to notify the player that this removal has occurred.
     * @param giveBack       Whether to give the item back.
     * @param playerToNotify Which player to notify for this removal.
     */
    public void removeItem(int slot, boolean notifyPlayer, boolean giveBack, @Nullable Player playerToNotify) {

        SellingItem i = getItemAt(slot);
        if (i != null) {

            removeItem(i, notifyPlayer, giveBack, playerToNotify);

        }

    }

    /**
     * Remove item for sale.
     * If the player is not online and needs to receive the item back, it will be lost.
     *
     * @param sellingItem    The item for sale.
     * @param notifyPlayer   Whether to notify the player that this removal has occurred.
     * @param giveBack       Whether to give the item back.
     * @param playerToNotify Which player to notify for this removal.
     */
    public void removeItem(@NotNull SellingItem sellingItem, boolean notifyPlayer, boolean giveBack, @Nullable Player playerToNotify) {

        Objects.requireNonNull(sellingItem);
        if (playerToNotify == null && notifyPlayer) {
            playerToNotify = Bukkit.getPlayer(sellingItem.getPlayerUUID());

        }

        if (items.remove(sellingItem) != null) {

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

    /**
     * Generates the item that explains whether the item for sale is sponsored or not.
     *
     * @param isSponsoring If the item will be sponsored.
     * @param sellingItem  The item for sale.
     * @return the item that explains whether the item for sale is sponsored or not.
     */
    public ItemStack generateMapItem(boolean isSponsoring, @Nullable SellingItem sellingItem) {

        ItemStack mapItem;

        if (sponsor != null && sponsor.equals(sellingItem)) {
            mapItem = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = mapItem.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());
            m.setLore(InternalUtil.CONFIGMANAGER.getSponsoredLore());
            mapItem.setItemMeta(m);
        } else if (canSponsor()) {

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

            m.setLore(InternalUtil.CONFIGMANAGER.getWaitToSponsor(getMissTimeInMins()));

            mapItem.setItemMeta(m);

        }
        return mapItem.clone();
    }

    /**
     * Sets a new sponsored item.
     *
     * @param sellingItem The new sponsored item.
     */
    public void setSponsor(@NotNull SellingItem sellingItem) {
        Objects.requireNonNull(sellingItem);

        applySponsor();
        sponsor = sellingItem;
        updateInventory();
        applyChangesDB();

    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Remove all items.
     */
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
        return new ArrayList<>(items.values());
    }

    public ArrayList<String> getOffMessages() {
        return new ArrayList<>(offMessages);
    }

    /**
     * Add a sale message made when the owner was offline.
     *
     * @param message sale message made when the owner was offline.
     */
    public void addMessage(@NotNull String message) {
        Objects.requireNonNull(message);

        offMessages.add(message);
        applyChangesDB();
    }

    /**
     * Remove all sale messages.
     */
    public void clearMessages() {
        offMessages.clear();
    }

    /**
     * Saves the changes to the database.
     */
    public void applyChangesDB() {

        CompletableFuture.runAsync(() -> RoadsideShops.saveShop(this));

    }

}
