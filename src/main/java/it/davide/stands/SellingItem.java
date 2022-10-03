package it.davide.stands;

import com.fren_gor.cmcSkyBlock.shop.SignUtilities;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import saving.ItemStackSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SellingItem implements Cloneable, ConfigurationSerializable {

    private final ItemStack i;

    private final ItemStack withpriceSeller;

    private final ItemStack withpriceBuyer;

    private final ItemStack forNewspaper;

    private final ItemStack withpriceESpondorSeller;

    private final ItemStack withpriceESpondorBuyer;

    private final int slot;

    private final double price;
    private final UUID p;

    public ItemStack getWithpriceBuyer() {
        return withpriceBuyer.clone();
    }

    public ItemStack getWithpriceESpondorBuyer() {
        return withpriceESpondorBuyer.clone();
    }

    public ItemStack getWithpriceESpondorSeller() {
        return withpriceESpondorSeller.clone();
    }

    public ItemStack getWithpriceSeller() {
        return withpriceSeller.clone();
    }

    public static SellingItem deserialize(Map<String, Object> args) {
        Validate.notNull(args, "Invalid args");

        return new SellingItem(ItemStackSerializer.deserializeItemStack((String) args.get("item")),
                (int) args.get("slot"), (double) args.get("price"), UUID.fromString((String) args.get("player")));
    }

    SellingItem(ItemStack i, int slot, double price, UUID pl) {

        this.i = i;
        this.slot = slot;
        this.price = price;
        this.p = pl;

        ItemMeta m = i.getItemMeta();
        ArrayList<String> p = new ArrayList<>();
        p.add("");
        p.add(ChatColor.translateAlternateColorCodes('&', StandManager.configconfig.getString("price-message")
                .replace("<value>", SignUtilities.formatVault(price))));
        p.add("");
        p.add(ChatColor.GOLD + "Click to edit item");
        if (m.getLore() != null)
            p.addAll(m.getLore());
        m.setLore(p);
        ItemStack h = i.clone();
        h.setItemMeta(m);
        withpriceSeller = h;

        p.clear();
        p.add("");
        p.add(ChatColor.translateAlternateColorCodes('&', StandManager.configconfig.getString("price-message")
                .replace("<value>", SignUtilities.formatVault(price))));
        p.add("");
        p.add(ChatColor.GOLD + "Click to buy item");

        ItemStack o = withpriceSeller.clone();
        ItemMeta s1 = withpriceSeller.getItemMeta().clone();
        s1.setLore(p);
        o.setItemMeta(s1);
        withpriceBuyer = o;

        ItemMeta ms = withpriceSeller.getItemMeta();
        ArrayList<String> p1 = new ArrayList<>();
        if (ms.getLore() != null)
            p1.addAll(ms.getLore());
        p1.remove(p1.size() - 1);
        p1.remove(p1.size() - 1);

        p1.add(ChatColor.AQUA + "Sponsored Item");
        p1.add("");
        p1.add(ChatColor.GOLD + "Click to edit item");

        ms.setLore(p1);
        ItemStack s = withpriceSeller.clone();
        s.setItemMeta(ms);
        withpriceESpondorSeller = s;
        p1.remove(p1.size() - 1);
        p1.add(ChatColor.GOLD + "Click to buy item");
        withpriceESpondorBuyer = withpriceESpondorSeller.clone();
        ItemMeta k = withpriceESpondorBuyer.getItemMeta();
        k.setLore(p1);
        withpriceESpondorBuyer.setItemMeta(k);

        String nome = Bukkit.getOfflinePlayer(this.p).getName();
        forNewspaper = withpriceSeller.clone();
        ms = forNewspaper.getItemMeta();
        ArrayList<String> ar = new ArrayList<>(ms.getLore());
        ar.remove(ar.size() - 1);
        ar.remove(ar.size() - 1);
        ar.add(ChatColor.YELLOW + "Owner: " + ChatColor.GRAY + nome);
        ar.add("");
        ar.add(ChatColor.GOLD + "Click to checkout " + nome + "'s stand");
        ms.setLore(ar);
        forNewspaper.setItemMeta(ms);

    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();
        map.put("item", ItemStackSerializer.serializeItemStack(i));
        map.put("slot", slot);
        map.put("price", price);
        map.put("player", p.toString());

        return map;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forNewspaper == null) ? 0 : forNewspaper.hashCode());
        result = prime * result + ((i == null) ? 0 : i.hashCode());
        result = prime * result + ((p == null) ? 0 : p.hashCode());
        long temp;
        temp = Double.doubleToLongBits(price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + slot;
        result = prime * result + ((withpriceBuyer == null) ? 0 : withpriceBuyer.hashCode());
        result = prime * result + ((withpriceESpondorBuyer == null) ? 0 : withpriceESpondorBuyer.hashCode());
        result = prime * result + ((withpriceESpondorSeller == null) ? 0 : withpriceESpondorSeller.hashCode());
        result = prime * result + ((withpriceSeller == null) ? 0 : withpriceSeller.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SellingItem)) {
            return false;
        }
        SellingItem other = (SellingItem) obj;
        if (forNewspaper == null) {
            if (other.forNewspaper != null) {
                return false;
            }
        } else if (!forNewspaper.equals(other.forNewspaper)) {
            return false;
        }
        if (i == null) {
            if (other.i != null) {
                return false;
            }
        } else if (!i.equals(other.i)) {
            return false;
        }
        if (p == null) {
            if (other.p != null) {
                return false;
            }
        } else if (!p.equals(other.p)) {
            return false;
        }
        if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (slot != other.slot) {
            return false;
        }
        if (withpriceBuyer == null) {
            if (other.withpriceBuyer != null) {
                return false;
            }
        } else if (!withpriceBuyer.equals(other.withpriceBuyer)) {
            return false;
        }
        if (withpriceESpondorBuyer == null) {
            if (other.withpriceESpondorBuyer != null) {
                return false;
            }
        } else if (!withpriceESpondorBuyer.equals(other.withpriceESpondorBuyer)) {
            return false;
        }
        if (withpriceESpondorSeller == null) {
            if (other.withpriceESpondorSeller != null) {
                return false;
            }
        } else if (!withpriceESpondorSeller.equals(other.withpriceESpondorSeller)) {
            return false;
        }
        if (withpriceSeller == null) {
            if (other.withpriceSeller != null) {
                return false;
            }
        } else if (!withpriceSeller.equals(other.withpriceSeller)) {
            return false;
        }
        return true;
    }

    public ItemStack getI() {
        return i;
    }

    public ItemStack getForNewspaper() {
        return forNewspaper;
    }

    public int getSlot() {
        return slot;
    }

    public double getPrice() {
        return price;
    }

    public UUID getP() {
        return p;
    }
}
