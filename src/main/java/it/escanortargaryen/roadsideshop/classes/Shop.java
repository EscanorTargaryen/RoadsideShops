package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Shop implements Cloneable, InventoryHolder {

    private final UUID playerUUID;

    private Inventory invSeller = null;

    private Inventory invBuyer = null;

    private final String playerName;

    private SellingItem sponsor = null;

    public final int maxSlots = 14;

//	private final int autoSlotsMax = 2;
    // private int autoSlots = 0;

    private int unlockedSlotsNumber = InternalUtil.CONFIGMANAGER.getUnlockedSlots();

    private ArrayList<SellingItem> items = new ArrayList<>();

    private final ArrayList<String> offMessages = new ArrayList<>();

    private final InventoryHolder holder = this;

    private long lastSponsor = 0L;

    public Shop(UUID playerUUID, String name, ArrayList<SellingItem> m, SellingItem sponsor) {

        this.playerUUID = playerUUID;
        playerName = name;
        items = m;
        this.sponsor = sponsor;
    }

    public Shop(Player player) {

        this.playerUUID = player.getUniqueId();
        playerName = player.getName();
    }

    public boolean canSponsor(long time) {
        Player pl = Bukkit.getPlayer(playerUUID);
        if (pl != null && pl.hasPermission("shop.bypass.sponsortime")) {

            return true;
        }

        return (time - lastSponsor) / 60000 > ConfigManager.SPONSORTIME;

    }

    public long getMissTimeinMins(long time) {
        long i = ConfigManager.SPONSORTIME - (time - lastSponsor) / 60000;
        if (i < 0) {

            return 0;
        }

        return i;

    }

    public void setTimeSponsor(long time) {

        lastSponsor = time;

    }

    public void openInventory(Player p, ViewMode mode) {

        if (mode == ViewMode.SELLER) {
            if (invSeller == null) {

                invSeller = getInventory();

            }

            p.openInventory(invSeller);

        } else {
            if (invBuyer == null) {

                invSeller = getInventory();

            }
            p.openInventory(invBuyer);

        }

    }

    public void updateInventory() {
        updateInvSeller();
        updateInvBuyer();

    }

    public void calculateSlots(Player p) {

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

            if (items != null)
                for (SellingItem s : items) {

                    if (s.equals(sponsor)) {

                        invSeller.setItem(s.getSlot(), s.getWithPriceAndSponsorSeller());

                    } else {

                        invSeller.setItem(s.getSlot(), s.getWithPriceSeller());

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

            if (items != null)
                for (SellingItem s : items) {

                    if (s.equals(sponsor)) {

                        invBuyer.setItem(s.getSlot(), s.getWithPriceAndSponsorBuyer());

                    } else {

                        invBuyer.setItem(s.getSlot(), s.getWithPriceBuyer());

                    }

                }
        }

    }

    public void addItem(SellingItem sellingItem) {
        addItem(sellingItem, false, false);

    }

    public void addItem(SellingItem sellingItem, boolean isSponsoring, boolean sendMessage) {
        addItem(sellingItem, isSponsoring, sendMessage, null);

    }

    public void addItem(SellingItem sellingItem, boolean isSponsoring, boolean notifyPlayer, Player p) {

        if (p == null && notifyPlayer) {
            p = Bukkit.getPlayer(sellingItem.getPlayerUUID());

        }

       /* if (items.size() == 0)
            invBuyer.setItem(1, new ItemStack(Material.AIR));*///TODO questa cosa ha senso?
        items.add(sellingItem);

        updateInventory();

        if (notifyPlayer && p != null)
            p.sendMessage(InternalUtil.CONFIGMANAGER.getPutItem(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

        if (isSponsoring) {
            if (notifyPlayer && p != null)
                p.sendMessage(InternalUtil.CONFIGMANAGER.getSponsorSet(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

            setSponsorItem(sellingItem);

        }

    }

    @Override
    public Shop clone() {
        try {
            return (Shop) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerUUID == null) ? 0 : playerUUID.hashCode());
        result = prime * result + ((playerName == null) ? 0 : playerName.hashCode());
        return result;
    }

    public void removeItem(int slot) {

        SellingItem i = getItemAt(slot);
        if (i != null) {

            removeItem(i, true, true, null);

        }

    }

    public void removeItem(SellingItem sellingItem, Player p) {

        Objects.requireNonNull(sellingItem);

        removeItem(sellingItem, true, true, p);

    }

    public void removeItem(int slot, boolean notifyPlayer, boolean giveBack, Player p) {

        SellingItem i = getItemAt(slot);
        if (i != null) {

            removeItem(i, notifyPlayer, giveBack, p);

        }

    }

    public void removeItem(SellingItem sellingItem, boolean notifyPlayer, boolean giveBack, Player p) {

        Objects.requireNonNull(sellingItem);
        if (sellingItem != null) {
            if (p == null && notifyPlayer) {
                p = Bukkit.getPlayer(sellingItem.getPlayerUUID());

            }

            if (items.remove(sellingItem)) {

                if (sellingItem.equals(sponsor)) {

                    sponsor = null;
                }
                updateInventory();

                if (giveBack && p != null) {

                    HashMap<Integer, ItemStack> r = p.getInventory().addItem(sellingItem.getItem());
                    if (r.values().size() > 0) {
                        if (notifyPlayer) {

                            p.sendMessage(InternalUtil.CONFIGMANAGER.getFullInvDrop());

                        }
                        for (ItemStack t : r.values()) {
                            p.getWorld().dropItemNaturally(p.getLocation(), t);

                        }
                    } else {

                        if (notifyPlayer) {

                            p.sendMessage(InternalUtil.CONFIGMANAGER.getRemoveItem(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

                        }

                    }
                }

            }

        }

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Shop)) {
            return false;
        }
        Shop other = (Shop) obj;
        if (playerUUID == null) {
            if (other.playerUUID != null) {
                return false;
            }
        } else if (!playerUUID.equals(other.playerUUID)) {
            return false;
        }
        if (playerName == null) {
            return other.playerName == null;
        } else return playerName.equals(other.playerName);
    }

    public ItemStack generateMapItem(boolean isSponsoring, SellingItem sellingItem) {
        ItemStack s;

        if (sponsor != null && sponsor.equals(sellingItem)) {
            s = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = s.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());
            m.setLore(InternalUtil.CONFIGMANAGER.getSponsoredLore());
            s.setItemMeta(m);
        } else if (canSponsor(System.currentTimeMillis())) {

            if (isSponsoring) {

                s = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = s.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

                if (sponsor != null) {
                    m.setLore(InternalUtil.CONFIGMANAGER.getSponsoringChange());
                } else {
                    m.setLore(InternalUtil.CONFIGMANAGER.getSponsoring());

                }
                s.setItemMeta(m);

            } else {

                s = new ItemStack(Material.PAPER);
                ItemMeta m = s.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

                if (sponsor != null) {
                    m.setLore(InternalUtil.CONFIGMANAGER.getNotSponsoringChange());
                } else {
                    m.setLore(InternalUtil.CONFIGMANAGER.getNotSponsoring());

                }
                s.setItemMeta(m);

            }

        } else {
            s = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = s.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

            m.setLore(InternalUtil.CONFIGMANAGER.getWaitToSponsor(getMissTimeinMins(System.currentTimeMillis())));

            s.setItemMeta(m);

        }
        return s.clone();
    }

    public void setSponsorItem(SellingItem sellingItem) {

        setTimeSponsor(System.currentTimeMillis());
        setSponsor(sellingItem);
        updateInventory();

    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void clear() {

        items.clear();
    }

    public Inventory getInvSeller() {
        return invSeller;
    }

    public Inventory getInvBuyer() {
        return invBuyer;
    }

    public SellingItem getSponsor() {
        return sponsor;
    }

    public void setSponsor(SellingItem sponsor) {
        this.sponsor = sponsor;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getUnlockedSlotsNumber() {
        return unlockedSlotsNumber;
    }

    public ArrayList<SellingItem> getItems() {
        return items;
    }

    public ArrayList<String> getOffMessages() {
        return offMessages;
    }

    public InventoryHolder getHolder() {
        return holder;
    }
}
