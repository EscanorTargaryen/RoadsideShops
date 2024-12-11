package it.escanortargaryen.roadsideshop.inventory;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.classes.ViewMode;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class that handles the item sale setting.
 */
public class SaleSettings implements InventoryHolder, Listener {

    /**
     * The shop where this item is present.
     */
    private final Shop shop;

    /**
     * The item that needs to be sold.
     */
    private final ItemStack itemToSell;

    /**
     * The slot number of the item.
     */
    private final int slotNumber;

    /**
     * If the price has been set.
     */
    private boolean isPriceSet = false;

    /**
     * If the items is set as sponsored.
     */
    private boolean isSponsoring = false;

    /**
     * If the player is setting the price right now.
     */
    private boolean settingPrice = false;

    /**
     * If the player needs to get out of this inventory.
     */
    private boolean exit = false;

    /**
     * The price of the item.
     */
    private double price = 0.0;

    /**
     * Creates a new SaleSettings.
     *
     * @param shop       The shop where this item is present.
     * @param itemToSell The item that needs to be sold.
     * @param player     The player that displays this menu.
     * @param slotNumber The slot number of the item.
     */
    public SaleSettings(@NotNull Shop shop, @NotNull ItemStack itemToSell, @NotNull Player player, int slotNumber) {

        Objects.requireNonNull(shop);
        Objects.requireNonNull(itemToSell);
        Objects.requireNonNull(player);

        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);

        this.shop = shop;
        this.slotNumber = slotNumber;
        this.itemToSell = itemToSell.clone();
        player.openInventory(getInventory());
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, InternalUtil.CONFIGMANAGER.getItemSettingsTitle());

        ItemStack item = itemToSell.clone();

        inv.setItem(10, item);

        inv.setItem(15, shop.generateMapItem(isSponsoring, null));
        ItemStack sellButton, priceButton;

        if (isPriceSet) {

            sellButton = new ItemStack(Material.GREEN_WOOL);
            ItemMeta mw = sellButton.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getSellButtonTitle());
            mw.setLore(InternalUtil.CONFIGMANAGER.getSellButtonLore());
            sellButton.setItemMeta(mw);

            priceButton = new ItemStack(Material.NAME_TAG);
            mw = priceButton.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getPriceButtonTitle(price));

            mw.setLore(InternalUtil.CONFIGMANAGER.getPriceButtonLore(price));
            priceButton.setItemMeta(mw);

        } else {

            sellButton = new ItemStack(Material.RED_WOOL);
            ItemMeta mw = sellButton.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getSellButtonTitleNotSet());
            mw.setLore(InternalUtil.CONFIGMANAGER.getSellButtonLoreNotSet());
            sellButton.setItemMeta(mw);

            priceButton = new ItemStack(Material.NAME_TAG);
            mw = priceButton.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getPriceButtonTitleNotSet());
            mw.setLore(InternalUtil.CONFIGMANAGER.getPriceButtonLoreNotSet());
            priceButton.setItemMeta(mw);

        }

        inv.setItem(24, sellButton);
        inv.setItem(6, priceButton);

        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        Inventory topInventory = InternalUtil.getTopInventory(e);

        Player player = (Player) e.getWhoClicked();

        if (topInventory.getHolder() != this)
            return;

        e.setCancelled(true);

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        if (e.getClickedInventory().getHolder() != this)
            return;

        if (e.getSlot() == 6) {
            settingPrice = true;
            player.closeInventory();

            new AnvilGUI.Builder().onClose(p -> {
                        settingPrice = false;

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                Player player = p.getPlayer();
                                player.openInventory(getInventory());

                            }
                        }.runTask(RoadsideShops.INSTANCE);

                    }).onClick((slot, stateSnapshot) -> { // called when the inventory output slot is clicked
                        if (slot != AnvilGUI.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        try {
                            price = Double.parseDouble(stateSnapshot.getText());
                            isPriceSet = price >= 0;

                            return List.of(AnvilGUI.ResponseAction.close());

                        } catch (NumberFormatException ff) {
                            return List.of(AnvilGUI.ResponseAction.replaceInputText(InternalUtil.CONFIGMANAGER.getWrongPrice()));

                        }

                    })
                    .itemLeft(new ItemStack(Material.GOLD_BLOCK)) // use a custom item for the first slot
                    .text(".")
                    .title(InternalUtil.CONFIGMANAGER.getAnvilTitle()) // set the title of the GUI (only works in 1.14+)
                    .plugin(RoadsideShops.INSTANCE) // set the plugin instance
                    .open(player);

        }

        if (e.getSlot() == 15) {

            if (shop.canSponsor()) {

                this.isSponsoring = !this.isSponsoring;

            }
            e.getInventory().setItem(15, shop.generateMapItem(isSponsoring, null));

        }
        if (e.getSlot() == 24) {

            if (isPriceSet) {

                SellingItem sellingItem = new SellingItem(itemToSell, slotNumber, price, shop.getPlayerUUID());

                shop.addItem(sellingItem, isSponsoring, true, player);

                exit = true;
                player.closeInventory();
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        shop.openInventory(player, ViewMode.SELLER);
                    }
                }.runTask(RoadsideShops.INSTANCE);

            }

        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {
            Player p = (Player) e.getPlayer();
            if (!settingPrice) {
                InventoryClickEvent.getHandlerList().unregister(this);
                InventoryCloseEvent.getHandlerList().unregister(this);
                p.closeInventory();
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        shop.openInventory(p, ViewMode.SELLER);
                    }
                }.runTask(RoadsideShops.INSTANCE);
            }
            if (!exit && !settingPrice) {
                HashMap<Integer, ItemStack> i = p.getInventory().addItem(itemToSell);

                if (i.size() > 0) {

                    p.sendMessage(InternalUtil.CONFIGMANAGER.getFullInvDrop());
                }

                for (ItemStack t : i.values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), t);

                }

            }
            if (exit) {
                InventoryClickEvent.getHandlerList().unregister(this);
                InventoryCloseEvent.getHandlerList().unregister(this);

            }

        }

    }

}
