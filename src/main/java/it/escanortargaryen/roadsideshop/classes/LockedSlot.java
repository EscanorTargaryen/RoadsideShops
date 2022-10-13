package it.escanortargaryen.roadsideshop.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LockedSlot {

    private final ItemStack itemStack;

    private final LockedSlotCheck lockedSlotCheck;

    public LockedSlot(ItemStack itemStack, LockedSlotCheck lockedSlotCheck) {
        this.itemStack = itemStack;
        this.lockedSlotCheck = lockedSlotCheck;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isLocked(Player player) {
        if (player != null) {
            return lockedSlotCheck.isLocked(player);

        }
        return true;

    }
}
