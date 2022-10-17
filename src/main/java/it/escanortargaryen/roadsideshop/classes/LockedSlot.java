package it.escanortargaryen.roadsideshop.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LockedSlot {

    private final ItemStack itemStack;

    private final LockedSlotCheck lockedSlotCheck;

    public LockedSlot(@NotNull ItemStack itemStack,@NotNull LockedSlotCheck lockedSlotCheck) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(lockedSlotCheck);

        this.itemStack = itemStack;
        this.lockedSlotCheck = lockedSlotCheck;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isLocked(@NotNull Player player) {

        Objects.requireNonNull(player);
        return lockedSlotCheck.isLocked(player);

    }
}
