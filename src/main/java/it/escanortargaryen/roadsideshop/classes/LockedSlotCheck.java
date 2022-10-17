package it.escanortargaryen.roadsideshop.classes;

import org.bukkit.entity.Player;

/**
 * Interface that contains the method that tells whether a custom slot is locked or not.
 */
public interface LockedSlotCheck {

    /**
     * Returns whether the slot is unlocked or not for a player.
     *
     * @param player A player.
     * @return whether the slot is unlocked or not for a player.
     */
    boolean isLocked(Player player);
}
