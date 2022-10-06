package it.escanortargaryen.roadsideshop;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.ArrayList;
import java.util.Arrays;

public class ItemSettingsIH implements InventoryHolder, Listener {

    private Shop shop;
    private ItemStack itemToSell;
    private int slotNumber;
    private boolean isPriceSet = false, isSponsoring = false, closeInv = false, exit = false;

    private double price = 0.0;

    public ItemSettingsIH(Shop shop, ItemStack itemToSell, Player p, int slotNumber) {
        Bukkit.getPluginManager().registerEvents(this, StandManager.getInstance());

        this.shop = shop;
        this.slotNumber = slotNumber;
        this.itemToSell = itemToSell.clone();
        p.openInventory(getInventory());
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, ChatColor.DARK_BLUE + "Selling Settings");

        ItemStack item = itemToSell.clone();
        ItemMeta d = item.getItemMeta().clone();
        ArrayList<String> arr = new ArrayList<>();
        arr.add("");
        arr.add(ChatColor.GRAY + "Item to be sold");
        d.setLore(arr);
        item.setItemMeta(d);

        inv.setItem(10, item);

        inv.setItem(15, InternalUtil.generateMapItem(shop, isSponsoring));
        ItemStack wool, prezzo;

        if (isPriceSet) {

            wool = new ItemStack(Material.GREEN_WOOL);
            ItemMeta mw = wool.getItemMeta();
            mw.setDisplayName(ChatColor.YELLOW + "Sell it");
            mw.setLore(Arrays.asList("", ChatColor.GOLD + "Click to finish up and sell the item"));
            wool.setItemMeta(mw);

            prezzo = new ItemStack(Material.NAME_TAG);
            mw = prezzo.getItemMeta();
            mw.setDisplayName(ChatColor.translateAlternateColorCodes('&', StandManager.configconfig
                    .getString("price-message").replace("<value>", price + "")));

            mw.setLore(Arrays.asList("", ChatColor.GOLD + "Click to change the price"));
            prezzo.setItemMeta(mw);

        } else {

            wool = new ItemStack(Material.RED_WOOL);
            ItemMeta mw = wool.getItemMeta();
            mw.setDisplayName(ChatColor.YELLOW + "Sell the item");
            mw.setLore(Arrays.asList("", ChatColor.DARK_RED + "You must set a price before selling it"));
            wool.setItemMeta(mw);

            prezzo = new ItemStack(Material.NAME_TAG);
            mw = prezzo.getItemMeta();
            mw.setDisplayName(ChatColor.YELLOW + "Set a price");
            mw.setLore(Arrays.asList("", ChatColor.GOLD + "Click to set a price for this item"));
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
            closeInv = true;

            new AnvilGUI.Builder().onClose(player -> {

                        isPriceSet = true;
                        closeInv = false;
                        player.openInventory(getInventory());

                    }).onComplete((player, text) -> { // called when the inventory output slot is clicked

                        try {
                            price = Double.parseDouble(text);
                            return AnvilGUI.Response.close();

                        } catch (NumberFormatException ff) {
                            return AnvilGUI.Response.text("Incorrect number");

                        }

                    }).preventClose().text("price") // prevents the inventory from being close
                    .itemLeft(new ItemStack(Material.GOLD_BLOCK)) // use a custom item for the first slot

                    .title("Enter the price here") // set the title of the GUI (only works in 1.14+)
                    .plugin(StandManager.getInstance()) // set the plugin instance
                    .open((Player) e.getWhoClicked());

        }

        if (e.getSlot() == 15) {

            if (shop.canSponsor(System.currentTimeMillis())) {

                if (!this.isSponsoring) {

                    this.isSponsoring = true;

                } else {

                    this.isSponsoring = false;
                }

            }
            e.getInventory().setItem(15, InternalUtil.generateMapItem(shop, isSponsoring));

        }
        if (e.getSlot() == 24) {

            if (isPriceSet) {
                closeInv = true;

                if (shop.getItems().size() == 0)
                    shop.getInvBuyer().setItem(1, new ItemStack(Material.AIR));
                SellingItem sellingItem = new SellingItem(itemToSell, slotNumber, price, shop.getPlayerUUID());
                shop.getItems().add(sellingItem);

                shop.getInvBuyer().setItem(sellingItem.getSlot(), sellingItem.getWithpriceBuyer());
                shop.getInvSeller().setItem(sellingItem.getSlot(), sellingItem.getWithpriceSeller());

                Player p = ((Player) e.getWhoClicked());
                p.closeInventory();
                Bukkit.dispatchCommand(p, "stand");
                exit = true;

                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        StandManager.configconfig.getString("put-item")
                                .replace("<price>", sellingItem.getPrice() + "")
                                .replace("<type>", sellingItem.getI().getType().toString().toLowerCase().replace("_", " "))
                                .replace("<amount>", sellingItem.getI().getAmount() + "")));

                if (isSponsoring) {

                    e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            StandManager.configconfig.getString("sponsor-set")));
                    if (shop.getSponsor() != null) {

                        shop.getInvBuyer().setItem(shop.getSponsor().getSlot(), shop.getSponsor().getWithpriceBuyer());
                        shop.getInvSeller().setItem(shop.getSponsor().getSlot(), shop.getSponsor().getWithpriceSeller());

                    }
                    shop.setTimeSponsor(System.currentTimeMillis());
                    shop.setSponsor(sellingItem);

                    shop.getInvBuyer().setItem(sellingItem.getSlot(), sellingItem.getWithpriceESpondorBuyer());
                    shop.getInvSeller().setItem(sellingItem.getSlot(), sellingItem.getWithpriceESpondorSeller());

                }

            }

        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (!closeInv) {
                e.getPlayer().getInventory().addItem(itemToSell);

                InventoryClickEvent.getHandlerList().unregister(this);
                InventoryCloseEvent.getHandlerList().unregister(this);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Bukkit.dispatchCommand(e.getPlayer(), "stand");
                    }
                }.runTask(StandManager.getInstance());

            }
            if (exit) {

                InventoryClickEvent.getHandlerList().unregister(this);
                InventoryCloseEvent.getHandlerList().unregister(this);

            }

        }

    }

}
