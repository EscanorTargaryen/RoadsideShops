package it.escanortargaryen.roadsideshop.events;

import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerBuyShopEvent extends Event implements Cancellable {

    private final Shop shop;

    private final SellingItem item;

    private final Player buyer;

    public PlayerBuyShopEvent(@NotNull Shop shop,@NotNull SellingItem sellingItem,@NotNull Player buyer) {

        Objects.requireNonNull(shop);
        Objects.requireNonNull(sellingItem);
        Objects.requireNonNull(buyer);

        this.shop = shop;
        item = sellingItem;
        this.buyer = buyer;

    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public Shop getShop() {
        return shop;
    }

    public SellingItem getItem() {
        return item;
    }

    public Player getBuyer() {
        return buyer;
    }

}
