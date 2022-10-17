package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.InternalUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Class representing an item for sale.
 */
public class SellingItem {

    /**
     * The item to be sold.
     */
    private final ItemStack item;

    /**
     * Item being shown to the seller.
     */
    private final ItemStack withPriceSeller;

    /**
     * Item being shown to the buyer.
     */
    private final ItemStack withPriceBuyer;

    /**
     * Item that is shown in the newspaper.
     */
    private final ItemStack forNewspaper;

    /**
     * Sponsored item being shown to the seller.
     */
    private final ItemStack withPriceAndSponsorSeller;

    /**
     * Sponsored item being shown to the buyer.
     */
    private final ItemStack withPriceAndSponsorBuyer;

    /**
     * The number of slot the item occupies in the store inventory.
     */
    private final int slot;

    /**
     * The price of the item.
     */
    private final double price;

    /**
     * The UUID of the owner.
     */
    private final UUID playerUUID;

    /**
     * Creates a new SellingItem.
     *
     * @param item        The item to be sold.
     * @param slot        The number of slot the item occupies in the store inventory.
     * @param price       The price of the item.
     * @param playerOwner The UUID of the owner.
     */

    public SellingItem(@NotNull ItemStack item, int slot, double price, @NotNull UUID playerOwner) {

        Objects.requireNonNull(item);
        Objects.requireNonNull(playerOwner);

        this.item = item;
        this.slot = slot;
        this.price = price;
        this.playerUUID = playerOwner;

        ItemMeta m = item.getItemMeta();
        Objects.requireNonNull(m);
        ArrayList<String> p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleSeller(price));
        m.setLore(p);
        ItemStack h = item.clone();
        h.setItemMeta(m);
        withPriceSeller = h;

        m = item.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleSellerSponsor(price));
        m.setLore(p);
        h = item.clone();
        h.setItemMeta(m);
        withPriceAndSponsorSeller = h;

        m = item.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleBuyer(price));
        m.setLore(p);
        h = item.clone();
        h.setItemMeta(m);
        withPriceBuyer = h;

        m = item.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getItemSaleBuyerSponsor(price));
        m.setLore(p);
        h = item.clone();
        h.setItemMeta(m);
        withPriceAndSponsorBuyer = h;

        String name = Bukkit.getOfflinePlayer(this.playerUUID).getName();

        m = item.getItemMeta();
        p = new ArrayList<>();
        if (m.getLore() != null) {
            p.addAll(m.getLore());

        }
        p.addAll(InternalUtil.CONFIGMANAGER.getLoreForNewspaper(price, name));
        m.setLore(p);
        h = item.clone();
        h.setItemMeta(m);
        forNewspaper = h;

    }

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

    public ItemStack getItem() {
        return item;
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

}
