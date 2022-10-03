package it.davide.stands;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerBuyStandEvent extends Event implements Cancellable {

    private final Stand stand;

    private final SellingItem item;

    private final Player buyer;

    public PlayerBuyStandEvent(Stand stand2, SellingItem c, Player whoClicked) {

        stand = stand2;
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
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public Stand getStand() {
        return stand;
    }

    public SellingItem getItem() {
        return item;
    }

    public Player getBuyer() {
        return buyer;
    }

}
