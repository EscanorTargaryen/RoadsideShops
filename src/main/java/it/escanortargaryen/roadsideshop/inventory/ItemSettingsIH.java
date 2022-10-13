package it.escanortargaryen.roadsideshop.inventory;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
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

import java.util.Objects;

public class ItemSettingsIH implements InventoryHolder, Listener {

    private final Shop shop;
    private final ItemStack itemToSell;
    private final int slotNumber;

    private boolean isPriceSet = false, isSponsoring = false, settingPrice = false, exit = false;

    private double price = 0.0;

    public ItemSettingsIH(Shop shop, ItemStack itemToSell, Player p, int slotNumber) {
        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);

        this.shop = shop;
        this.slotNumber = slotNumber;
        this.itemToSell = itemToSell.clone();
        p.openInventory(getInventory());
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, InternalUtil.CONFIGMANAGER.getItemSettingsTitle());

        ItemStack item = itemToSell.clone();

        inv.setItem(10, item);

        inv.setItem(15, InternalUtil.generateMapItem(shop, isSponsoring, null));
        ItemStack wool, prezzo;

        if (isPriceSet) {

            wool = new ItemStack(Material.GREEN_WOOL);
            ItemMeta mw = wool.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getSellButtonTitle());
            mw.setLore(InternalUtil.CONFIGMANAGER.getSellButtonLore());
            wool.setItemMeta(mw);

            prezzo = new ItemStack(Material.NAME_TAG);
            mw = prezzo.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getPriceButtonTitle(price));

            mw.setLore(InternalUtil.CONFIGMANAGER.getPriceButtonLore(price));
            prezzo.setItemMeta(mw);

        } else {

            wool = new ItemStack(Material.RED_WOOL);
            ItemMeta mw = wool.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getSellButtonTitleNotSet());
            mw.setLore(InternalUtil.CONFIGMANAGER.getSellButtonLoreNotSet());
            wool.setItemMeta(mw);

            prezzo = new ItemStack(Material.NAME_TAG);
            mw = prezzo.getItemMeta();
            Objects.requireNonNull(mw).setDisplayName(InternalUtil.CONFIGMANAGER.getPriceButtonTitleNotSet());
            mw.setLore(InternalUtil.CONFIGMANAGER.getPriceButtonLoreNotSet());
            prezzo.setItemMeta(mw);

        }

        inv.setItem(24, wool);
        inv.setItem(6, prezzo);

        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this)

            return;

        e.setCancelled(true);

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        if (e.getClickedInventory().getHolder() != this)
            return;

        if (e.getSlot() == 6) {
            settingPrice = true;
            e.getWhoClicked().closeInventory();

            new AnvilGUI.Builder().onClose(player -> {
                        settingPrice = false;

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                player.openInventory(getInventory());
                            }
                        }.runTask(RoadsideShops.INSTANCE);

                    }).onComplete((player, text) -> { // called when the inventory output slot is clicked

                        try {
                            price = Double.parseDouble(text);
                            isPriceSet = true;
                            return AnvilGUI.Response.close();

                        } catch (NumberFormatException ff) {
                            return AnvilGUI.Response.text(InternalUtil.CONFIGMANAGER.getWrongPrice());

                        }

                    })
                    .itemLeft(new ItemStack(Material.GOLD_BLOCK)) // use a custom item for the first slot
                    .text(".")
                    .title(InternalUtil.CONFIGMANAGER.getAnvilTitle()) // set the title of the GUI (only works in 1.14+)
                    .plugin(RoadsideShops.INSTANCE) // set the plugin instance
                    .open((Player) e.getWhoClicked());

        }

        if (e.getSlot() == 15) {

            if (shop.canSponsor(System.currentTimeMillis())) {

                this.isSponsoring = !this.isSponsoring;

            }
            e.getInventory().setItem(15, InternalUtil.generateMapItem(shop, isSponsoring, null));

        }
        if (e.getSlot() == 24) {

            if (isPriceSet) {

                if (shop.getItems().size() == 0)
                    shop.getInvBuyer().setItem(1, new ItemStack(Material.AIR));
                SellingItem sellingItem = new SellingItem(itemToSell, slotNumber, price, shop.getPlayerUUID());
                shop.getItems().add(sellingItem);

                shop.getInvBuyer().setItem(sellingItem.getSlot(), sellingItem.getWithPriceBuyer());
                shop.getInvSeller().setItem(sellingItem.getSlot(), sellingItem.getWithPriceSeller());
                exit = true;
                Player p = ((Player) e.getWhoClicked());
                p.closeInventory();

                p.sendMessage(InternalUtil.CONFIGMANAGER.getPutItem(sellingItem.getPrice(), sellingItem.getI().getType().toString(), sellingItem.getI().getAmount()));

                if (isSponsoring) {

                    e.getWhoClicked().sendMessage(InternalUtil.CONFIGMANAGER.getSponsorSet(price, sellingItem.getI().getType().toString(), sellingItem.getI().getAmount()));

                    InternalUtil.setSponsorItem(shop, sellingItem);

                }
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Bukkit.dispatchCommand(p, ConfigManager.SHOPCOMMAND);
                    }
                }.runTask(RoadsideShops.INSTANCE);

            }

        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (!settingPrice) {
                InventoryClickEvent.getHandlerList().unregister(this);
                InventoryCloseEvent.getHandlerList().unregister(this);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Bukkit.dispatchCommand(e.getPlayer(), ConfigManager.SHOPCOMMAND);
                    }
                }.runTask(RoadsideShops.INSTANCE);
            }
            if(!exit && !settingPrice){
                e.getPlayer().getInventory().addItem(itemToSell);
            }
            if (exit) {
                InventoryClickEvent.getHandlerList().unregister(this);
                InventoryCloseEvent.getHandlerList().unregister(this);

            }


        }

    }

}
