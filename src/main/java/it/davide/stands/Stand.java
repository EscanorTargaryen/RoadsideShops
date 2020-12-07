package it.davide.stands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import it.davide.advancementAddOn.AdvancementAddOnUtils;
import lombok.Getter;
import lombok.Setter;

public class Stand implements Cloneable, ConfigurationSerializable, InventoryHolder {
    @Getter

    private UUID p;
    @Getter
    private Inventory invSeller = null;

    @Getter
    private Inventory invBuyer = null;

    @Getter
    private String playerName;

    @Getter
    @Setter
    private SellingItem sponsor = null;

    public final int normalSlotsMax = 14;

//	private final int autoSlotsMax = 2;
    // private int autoSlots = 0;

    final private int defaultSlot = 3;
    @Getter
    private int normalSlots = 3;
    @Getter
    private ArrayList<SellingItem> items = new ArrayList<>();

    @Getter
    private ArrayList<String> offMessages = new ArrayList<>();

    @Getter

    private InventoryHolder holder = this;
    static public long timesponsor;
    private long lastsponsor = 0L;

    static {

        new BukkitRunnable() {

            @Override
            public void run() {
                Stand.timesponsor = StandManager.configconfig.getLong("sponsor-time-mills");

            }
        }.runTaskLater(StandManager.getInstance(), 40);
    }

    public boolean Checktime(long time) {
        Player pl = Bukkit.getPlayer(p);
        if (pl != null && pl.hasPermission("stand.bypass.sponsortime")) {

            return true;
        }

        if ((time - lastsponsor) > timesponsor) {

            return true;

        }

        return false;

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
                StandManager.getInstance().saveStand(this);

            } else {
                StandManager.getInstance().saveStand(this);

                p.openInventory(invSeller);

            }
        else {
            if (invBuyer == null) {

                invSeller = getInventory();
                p.openInventory(invBuyer);

            } else
                p.openInventory(invBuyer);

        }

    }

    public void updateInventory() {

        invSeller = getInventory();

    }

    public void calculateSlots(Player p) {

        int slot = defaultSlot;

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

        if (slot >= normalSlotsMax) {

            normalSlots = normalSlotsMax;
        } else

            normalSlots = slot;


        updateInventory();

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
    public Inventory getInventory() {

        invSeller = Bukkit.createInventory(this, 18, ChatColor.DARK_BLUE + playerName + "'s stand");

        ItemStack here = StandManager.unlockedslot;

        for (int i = 0; i < 18; i++) {

            invSeller.setItem(i, StandManager.not);
        }

        for (int i = 1; i < normalSlots + 1; i++) {

            if (i > 7) {

                invSeller.setItem(i + 2, here);

            } else {
                invSeller.setItem(i, here);

            }

        }

        invSeller.setItem(0, StandManager.log);
        invSeller.setItem(8, StandManager.log);
        invSeller.setItem(9, StandManager.log);
        invSeller.setItem(17, StandManager.log);

        if (items != null)
            for (SellingItem s : items) {

                if (sponsor != null && s.equals(sponsor)) {

                    invSeller.setItem(s.getSlot(), s.getWithpriceESpondorSeller());

                } else {

                    invSeller.setItem(s.getSlot(), s.getWithpriceSeller());

                }

            }

        invBuyer = Bukkit.createInventory(this, 18, ChatColor.DARK_BLUE + playerName + "'s stand");

        invBuyer.setItem(0, StandManager.log);
        invBuyer.setItem(8, StandManager.log);
        invBuyer.setItem(9, StandManager.log);
        invBuyer.setItem(17, StandManager.log);

        if (items != null)
            for (SellingItem s : items) {

                if (sponsor != null && s.equals(sponsor)) {

                    invBuyer.setItem(s.getSlot(), s.getWithpriceESpondorBuyer());

                } else {

                    invBuyer.setItem(s.getSlot(), s.getWithpriceBuyer());

                }

            }

        return invSeller;
    }

    public boolean canSell(int slot) {

        if (slot > 0 && slot < 8) {

            if (slot <= normalSlots)
                return true;

        } else if (slot > 9 && slot < 17) {

            int temp = slot - 2;

            if (temp <= normalSlots)
                return true;
        }

        return false;
    }

    public Stand(UUID p, String name, ArrayList<SellingItem> m, SellingItem sponsor) {

        this.p = p;
        playerName = name;
        items = m;
        this.sponsor = sponsor;
    }

    public Stand(UUID p, String name) {

        this.p = p;
        playerName = name;
    }

    @SuppressWarnings("unchecked")
    public static Stand deserialize(Map<String, Object> args) {
        Validate.notNull(args, "Invalid args");

        return new Stand(UUID.fromString((String) args.get("playerUUID")), (String) args.get("playername"),
                (ArrayList<SellingItem>) args.get("items"), (SellingItem) args.get("sponsor"));
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();
        map.put("playerUUID", p.toString());
        map.put("items", items);
        map.put("playername", playerName);
        map.put("sponsor", sponsor);
        return map;
    }

    @Override
    public Stand clone() {
        try {
            return (Stand) super.clone();
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
        if (!(obj instanceof Stand)) {
            return false;
        }
        Stand other = (Stand) obj;
        if (p == null) {
            if (other.p != null) {
                return false;
            }
        } else if (!p.equals(other.p)) {
            return false;
        }
        if (playerName == null) {
            if (other.playerName != null) {
                return false;
            }
        } else if (!playerName.equals(other.playerName)) {
            return false;
        }
        return true;
    }

}
