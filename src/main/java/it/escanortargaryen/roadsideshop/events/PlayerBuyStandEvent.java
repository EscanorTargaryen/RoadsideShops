package it.escanortargaryen.roadsideshop.events;

import it.escanortargaryen.roadsideshop.SellingItem;
import it.escanortargaryen.roadsideshop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerBuyStandEvent extends Event implements Cancellable {

    private final Shop shop;

    private final SellingItem item;

    private final Player buyer;

    public PlayerBuyStandEvent(Shop shop2, SellingItem c, Player whoClicked) {

        shop = shop2;
        item = c;
        buyer = whoClicked;

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

    public Shop getStand() {
        return shop;
    }

    public SellingItem getItem() {
        return item;
    }

    public Player getBuyer() {
        return buyer;
    }

}
