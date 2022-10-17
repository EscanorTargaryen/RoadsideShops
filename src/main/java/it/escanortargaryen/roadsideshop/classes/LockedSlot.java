package it.escanortargaryen.roadsideshop.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class representing a custom locked slot.
 */
public class LockedSlot {

    /**
     * The item that will be displayed when the slot is locked.
     */
    private final ItemStack itemStack;

    /**
     * This interface contains the method that tells whether the slot is unlocked or not.
     */
    private final LockedSlotCheck lockedSlotCheck;

    /**
     * Creates a new LockedSlot.
     *
     * @param itemStack       The item that will be displayed when the slot is locked.
     * @param lockedSlotCheck This interface contains the method that tells whether the slot is unlocked or not.
     */
    public LockedSlot(@NotNull ItemStack itemStack, @NotNull LockedSlotCheck lockedSlotCheck) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(lockedSlotCheck);

        this.itemStack = itemStack;
        this.lockedSlotCheck = lockedSlotCheck;
    }

    /**
     * Returns the item that will be displayed when the slot is locked.
     *
     * @return the item that will be displayed when the slot is locked.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Returns whether the slot is unlocked or not for a player.
     *
     * @param player A player.
     * @return whether the slot is unlocked or not for a player.
     */
    public boolean isLocked(@NotNull Player player) {

        Objects.requireNonNull(player);
        return lockedSlotCheck.isLocked(player);

    }
}
