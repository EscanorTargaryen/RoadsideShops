package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.RoadsideShops;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Shop implements Cloneable, ConfigurationSerializable, InventoryHolder {

    private final UUID p;

    private Inventory invSeller = null;

    private Inventory invBuyer = null;

    private final String playerName;

    private SellingItem sponsor = null;

    public final int normalSlotsMax = 14;

//	private final int autoSlotsMax = 2;
    // private int autoSlots = 0;

    final private int defaultSlot = 3;

    private int normalSlots = 3;

    private ArrayList<SellingItem> items = new ArrayList<>();

    private final ArrayList<String> offMessages = new ArrayList<>();

    private final InventoryHolder holder = this;
    static public long timesponsor;
    private long lastsponsor = 0L;

    static {

        new BukkitRunnable() {

            @Override
            public void run() {
                Shop.timesponsor = RoadsideShops.CONFIGMANAGER.getSponsorTimeMills();

            }
        }.runTaskLater(RoadsideShops.getInstance(), 40);
    }

    public boolean canSponsor(long time) {
        Player pl = Bukkit.getPlayer(p);
        if (pl != null && pl.hasPermission("stand.bypass.sponsortime")) {

            return true;
        }

        return (time - lastsponsor) > timesponsor;

    }

    public long getMissTimeinMins(long time) {
        long i = (timesponsor - (time - lastsponsor)) / 60000;
        if (i < 0) {

            return 0;
        }

        return i;

    }

    public void setTimeSponsor(long time) {

        lastsponsor = time;

    }

    public void openInventory(Player p, String mode) {

        if (mode.equals("seller"))
            if (invSeller == null) {
                calculateSlots(p);
                invSeller = getInventory();

                p.openInventory(invSeller);
                RoadsideShops.getInstance().saveStand(this);

            } else {
                RoadsideShops.getInstance().saveStand(this);

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

        invSeller = Bukkit.createInventory(this, 18, ChatColor.DARK_BLUE + playerName + "'s stand");

        ItemStack here = RoadsideShops.unlockedslot;

        for (int i = 0; i < 18; i++) {

            invSeller.setItem(i, RoadsideShops.not);
        }

        for (int i = 1; i < normalSlots + 1; i++) {

            if (i > 7) {

                invSeller.setItem(i + 2, here);

            } else {
                invSeller.setItem(i, here);

            }

        }

        invSeller.setItem(0, RoadsideShops.log);
        invSeller.setItem(8, RoadsideShops.log);
        invSeller.setItem(9, RoadsideShops.log);
        invSeller.setItem(17, RoadsideShops.log);

        if (items != null)
            for (SellingItem s : items) {

                if (s.equals(sponsor)) {

                    invSeller.setItem(s.getSlot(), s.getWithpriceESpondorSeller());

                } else {

                    invSeller.setItem(s.getSlot(), s.getWithpriceSeller());

                }

            }

        invBuyer = Bukkit.createInventory(this, 18, ChatColor.DARK_BLUE + playerName + "'s stand");

        invBuyer.setItem(0, RoadsideShops.log);
        invBuyer.setItem(8, RoadsideShops.log);
        invBuyer.setItem(9, RoadsideShops.log);
        invBuyer.setItem(17, RoadsideShops.log);

        if (items != null)
            for (SellingItem s : items) {

                if (s.equals(sponsor)) {

                    invBuyer.setItem(s.getSlot(), s.getWithpriceESpondorBuyer());

                } else {

                    invBuyer.setItem(s.getSlot(), s.getWithpriceBuyer());

                }

            }

        return invSeller;
    }

    public boolean canSell(int slot) {

        if (slot > 0 && slot < 8) {

            return slot <= normalSlots;

        } else if (slot > 9 && slot < 17) {

            int temp = slot - 2;

            return temp <= normalSlots;
        }

        return false;
    }

    public Shop(UUID p, String name, ArrayList<SellingItem> m, SellingItem sponsor) {

        this.p = p;
        playerName = name;
        items = m;
        this.sponsor = sponsor;
    }

    public Shop(UUID p, String name) {

        this.p = p;
        playerName = name;
    }


    public static Shop deserialize(Map<String, Object> args) {
        Validate.notNull(args, "Invalid args");

        return new Shop(UUID.fromString((String) args.get("playerUUID")), (String) args.get("playername"),
                (ArrayList<SellingItem>) args.get("items"), (SellingItem) args.get("sponsor"));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();
        map.put("playerUUID", p.toString());
        map.put("items", items);
        map.put("playername", playerName);
        map.put("sponsor", sponsor);
        return map;
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
        result = prime * result + ((p == null) ? 0 : p.hashCode());
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
        if (p == null) {
            if (other.p != null) {
                return false;
            }
        } else if (!p.equals(other.p)) {
            return false;
        }
        if (playerName == null) {
            return other.playerName == null;
        } else return playerName.equals(other.playerName);
    }

    public UUID getPlayerUUID() {
        return p;
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

    public int getNormalSlots() {
        return normalSlots;
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
