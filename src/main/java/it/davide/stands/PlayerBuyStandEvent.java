package it.davide.stands;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class PlayerBuyStandEvent extends Event implements Cancellable {

	@Getter
	private final Stand stand;

	@Getter
	private final SellingItem item;

	@Getter
	private final Player buyer;

	public PlayerBuyStandEvent(Stand stand2, SellingItem c, Player whoClicked) {

	stand=stand2;
	item=c;
	buyer=whoClicked;
	
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

	@Setter
	@Getter
	private boolean cancelled;

}
