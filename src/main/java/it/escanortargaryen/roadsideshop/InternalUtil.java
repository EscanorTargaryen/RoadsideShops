package it.escanortargaryen.roadsideshop;

import it.escanortargaryen.roadsideshop.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InternalUtil {

    public static ConfigManager CONFIGMANAGER;
    public static ArrayList<InventoryHolder> INVENTORYHOLDERS = new ArrayList<>();
    public static ItemStack BACKARROW;
    public static ItemStack UNLOCKEDSLOT = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    public static ItemStack LOCKEDSLOT;
    public static ItemStack LOG;
    public static ItemStack RIGHTARROW, LEFTARROW;

    InternalUtil() {
        CONFIGMANAGER = new ConfigManager(RoadsideShops.INSTANCE);

        BACKARROW = new ItemStack(Material.ARROW);
        ItemMeta ws = BACKARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(InternalUtil.CONFIGMANAGER.getBackButtonTitle());
        ws.setLore(InternalUtil.CONFIGMANAGER.getBackButtonLore());
        BACKARROW.setItemMeta(ws);

        UNLOCKEDSLOT = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta h = UNLOCKEDSLOT.getItemMeta();
        Objects.requireNonNull(h).setDisplayName(CONFIGMANAGER.getUnlockedSlotPanelTitle());

        h.setLore(CONFIGMANAGER.getUnlockedSlotPanelLore());
        UNLOCKEDSLOT.setItemMeta(h);

        LOCKEDSLOT = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta w = LOCKEDSLOT.getItemMeta();
        Objects.requireNonNull(w).setDisplayName(CONFIGMANAGER.getLockedSlotPanelTitle());

        w.setLore(CONFIGMANAGER.getLockedSlotPanelLore());
        LOCKEDSLOT.setItemMeta(w);

        LOG = new ItemStack(Material.OAK_LOG);
        ws = LOG.getItemMeta();
        Objects.requireNonNull(ws).setLore(List.of("§c§c§c§c§c§c§c§c§c§c§c§c"));
        Objects.requireNonNull(ws).setDisplayName(ChatColor.WHITE + "");
        LOG.setItemMeta(ws);

        RIGHTARROW = new ItemStack(Material.ARROW);
        ws = RIGHTARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(CONFIGMANAGER.getRightArrowTitle());
        ws.setLore(CONFIGMANAGER.getRightArrowLore());
        RIGHTARROW.setItemMeta(ws);

        LEFTARROW = new ItemStack(Material.ARROW);
        ws = LEFTARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(CONFIGMANAGER.getLeftArrowTitle());
        ws.setLore(CONFIGMANAGER.getLeftArrowLore());
        LEFTARROW.setItemMeta(ws);

    }

    /**
     * In API versions 1.20.6 and earlier, InventoryView is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Inventory getTopInventory(InventoryClickEvent event) {
        try {
            Object view = event.getView();
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCursor(InventoryClickEvent event, ItemStack replace) {
        try {

            Object view = event.getView();

            Method setCursorMethod = view.getClass().getMethod("setCursor", ItemStack.class);

            setCursorMethod.setAccessible(true);

            setCursorMethod.invoke(view, replace);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
