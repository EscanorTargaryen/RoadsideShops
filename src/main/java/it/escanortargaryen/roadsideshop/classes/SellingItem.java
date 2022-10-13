package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class SellingItem implements Cloneable {

    private final ItemStack i;

    private final ItemStack withPriceSeller;

    private final ItemStack withPriceBuyer;

    private final ItemStack forNewspaper;

    private final ItemStack withPriceAndSponsorSeller;

    private final ItemStack withPriceAndSponsorBuyer;

    private final int slot;

    private final double price;
    private final UUID playerUUID;

    public ItemStack getWithPriceBuyer() {
        return withPriceBuyer.clone();
    }

    public ItemStack getWithPriceAndSponsorBuyer() {
        return withPriceAndSponsorBuyer.clone();
    }

    public ItemStack getWithPriceAndSponsorSeller() {
        return withPriceAndSponsorSeller.clone();
    }

    public ItemStack getWithPriceSeller() {
        return withPriceSeller.clone();
    }

    public SellingItem(ItemStack i, int slot, double price, UUID pl) {

        this.i = i;
        this.slot = slot;
        this.price = price;
        this.playerUUID = pl;

        ItemMeta m = i.getItemMeta();
        Objects.requireNonNull(m);
        ArrayList<String> p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleSeller(price));
        m.setLore(p);
        ItemStack h = i.clone();
        h.setItemMeta(m);
        withPriceSeller = h;

        m = i.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleSellerSponsor(price));
        m.setLore(p);
        h = i.clone();
        h.setItemMeta(m);
        withPriceAndSponsorSeller = h;

        m = i.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleBuyer(price));
        m.setLore(p);
        h = i.clone();
        h.setItemMeta(m);
        withPriceBuyer = h;

        m = i.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleBuyerSponsor(price));
        m.setLore(p);
        h = i.clone();
        h.setItemMeta(m);
        withPriceAndSponsorBuyer = h;

        String name = Bukkit.getOfflinePlayer(this.playerUUID).getName();

        m = i.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getLoreForNewspaper(price, name));
        m.setLore(p);
        h = i.clone();
        h.setItemMeta(m);
        forNewspaper = h;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forNewspaper == null) ? 0 : forNewspaper.hashCode());
        result = prime * result + ((i == null) ? 0 : i.hashCode());
        result = prime * result + ((playerUUID == null) ? 0 : playerUUID.hashCode());
        long temp;
        temp = Double.doubleToLongBits(price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + slot;
        result = prime * result + ((withPriceBuyer == null) ? 0 : withPriceBuyer.hashCode());
        result = prime * result + ((withPriceAndSponsorBuyer == null) ? 0 : withPriceAndSponsorBuyer.hashCode());
        result = prime * result + ((withPriceAndSponsorSeller == null) ? 0 : withPriceAndSponsorSeller.hashCode());
        result = prime * result + ((withPriceSeller == null) ? 0 : withPriceSeller.hashCode());
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
        if (playerUUID == null) {
            if (other.playerUUID != null) {
                return false;
            }
        } else if (!playerUUID.equals(other.playerUUID)) {
            return false;
        }
        if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (slot != other.slot) {
            return false;
        }
        if (withPriceBuyer == null) {
            if (other.withPriceBuyer != null) {
                return false;
            }
        } else if (!withPriceBuyer.equals(other.withPriceBuyer)) {
            return false;
        }
        if (withPriceAndSponsorBuyer == null) {
            if (other.withPriceAndSponsorBuyer != null) {
                return false;
            }
        } else if (!withPriceAndSponsorBuyer.equals(other.withPriceAndSponsorBuyer)) {
            return false;
        }
        if (withPriceAndSponsorSeller == null) {
            if (other.withPriceAndSponsorSeller != null) {
                return false;
            }
        } else if (!withPriceAndSponsorSeller.equals(other.withPriceAndSponsorSeller)) {
            return false;
        }
        if (withPriceSeller == null) {
            return other.withPriceSeller == null;
        } else return withPriceSeller.equals(other.withPriceSeller);
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

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public SellingItem clone() {
        try {
            //copy mutable state here, so the clone can't change the internals of the original
            return (SellingItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
