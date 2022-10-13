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

        if (mode == ViewMode.SELLER)
            if (invSeller == null) {
                calculateSlots(p);
                invSeller = getInventory();

                p.openInventory(invSeller);

            } else {

                p.openInventory(invSeller);

            }
        else {
            if (invBuyer == null) {

                invSeller = getInventory();

            }
            p.openInventory(invBuyer);

        }

    }

    public void updateInventory() {

        invSeller = getInventory();

    }

    public void calculateSlots(Player p) {

       /*  int slot = defaultSlot;

       for (Entry<String, Integer> s : StandManager.getAdvancementSlot().entrySet()) {

            if (AdvancementAddOnUtils.isAchievementGranted(p, s.getKey())) {

                slot += s.getValue();

            }

        }

        for (Entry<String, Integer> s : StandManager.getAdvancementPerms().entrySet()) {

            if (p.hasPermission(s.getKey())) {

                slot += s.getValue();

            }

        }

        normalSlots = Math.min(slot, normalSlotsMax);

        updateInventory();*/

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

        invBuyer = Bukkit.createInventory(this, 18, InternalUtil.CONFIGMANAGER.getShopTitle(playerName));

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

        return invSeller;
    }

    public void addItem(SellingItem sellingItem, boolean isSponsoring, boolean sendMessage, Player p) {

        if (p == null && sendMessage) {
            p = Bukkit.getPlayer(sellingItem.getPlayerUUID());

        }

        if (items.size() == 0)
            invBuyer.setItem(1, new ItemStack(Material.AIR));//TODO questa cosa ha senso?
        items.add(sellingItem);

        invBuyer.setItem(sellingItem.getSlot(), sellingItem.getWithPriceBuyer());
        invBuyer.setItem(sellingItem.getSlot(), sellingItem.getWithPriceSeller());
        if (sendMessage && p != null)
            p.sendMessage(InternalUtil.CONFIGMANAGER.getPutItem(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

        if (isSponsoring) {
            if (sendMessage && p != null)
                p.sendMessage(InternalUtil.CONFIGMANAGER.getSponsorSet(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

            setSponsorItem(sellingItem);

        }

    }

    public boolean canSell(int slot) {

        if (slot > 0 && slot < 8) {

            return slot <= unlockedSlotsNumber;

        } else if (slot > 9 && slot < 17) {

            int temp = slot - 2;

            return temp <= unlockedSlotsNumber;
        }

        return false;
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
        if (sponsor != null) {

            invBuyer.setItem(sponsor.getSlot(), sponsor.getWithPriceBuyer());
            invSeller.setItem(sponsor.getSlot(), sponsor.getWithPriceSeller());

        }
        setTimeSponsor(System.currentTimeMillis());
        setSponsor(sellingItem);

        invBuyer.setItem(sellingItem.getSlot(), sellingItem.getWithPriceAndSponsorBuyer());
        invSeller.setItem(sellingItem.getSlot(), sellingItem.getWithPriceAndSponsorSeller());

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
