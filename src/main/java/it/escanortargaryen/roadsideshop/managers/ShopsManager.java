package it.escanortargaryen.roadsideshop.managers;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.classes.ViewMode;
import it.escanortargaryen.roadsideshop.db.DatabaseManager;
import it.escanortargaryen.roadsideshop.events.PlayerBuyShopEvent;
import it.escanortargaryen.roadsideshop.inventory.ItemSettings;
import it.escanortargaryen.roadsideshop.inventory.SaleSettings;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

/**
 * Manager who manages clicks within shop inventories.
 */
public class ShopsManager implements Listener {

    private int itemsSold = 0;

    public ShopsManager(@Nullable Metrics metrics) {
        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);
        if (metrics != null)
            metrics.addCustomChart(new SingleLineChart("new_items_sold", () -> {
                int t = itemsSold;
                itemsSold = 0;
                return t;
            }));

    }

    private Shop getShop(@NotNull InventoryHolder inventoryHolder) {

        Objects.requireNonNull(inventoryHolder);

        for (Shop s : DatabaseManager.getCachedShops()) {

            if (s.getInventoryHolder() == inventoryHolder)
                return s;
        }
        return null;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        Inventory topInventory = InternalUtil.getTopInventory(e);

        Player player = (Player) e.getWhoClicked();

        if (!InternalUtil.INVENTORYHOLDERS.contains(topInventory.getHolder()))
            return;

        if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        Shop shop = getShop(Objects.requireNonNull(topInventory.getHolder()));
        SellingItem sellingItem = Objects.requireNonNull(shop).getItemAt(e.getSlot());

        if (shop.getViewers().get(player) == ViewMode.SELLER) {
            if (e.getClickedInventory().getHolder() != topInventory.getHolder()) {

                if (e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT
                        || e.getClick() == ClickType.SHIFT_RIGHT) {

                    e.setCancelled(true);

                }
                return;
            }

            e.setCancelled(true);

            if (sellingItem == null) {

                if (e.getCurrentItem().equals(InternalUtil.UNLOCKEDSLOT)) {

                    if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                        return;
                    }

                    ItemStack i = e.getCursor().clone();
                    InternalUtil.setCursor(e, new ItemStack(Material.AIR));

                    new SaleSettings(shop, i.clone(), player, e.getSlot());

                }

            } else {

                new ItemSettings(shop, sellingItem, player);

            }

        } else {
            e.setCancelled(true);

            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

                if (sellingItem != null) {
                    if (RoadsideShops.getEconomy().has(player, sellingItem.getPrice())) {

                        PlayerBuyShopEvent ev = new PlayerBuyShopEvent(shop, sellingItem, player);

                        Bukkit.getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {

                            HashMap<Integer, ItemStack> i = player.getInventory().addItem(sellingItem.getItem());

                            if (i.size() > 0) {

                                player.sendMessage(InternalUtil.CONFIGMANAGER.getFullInvNoDrop());
                            } else {

                                RoadsideShops.getEconomy().withdrawPlayer(player, sellingItem.getPrice());
                                RoadsideShops.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(shop.getPlayerUUID()), sellingItem.getPrice());
                                itemsSold++;
                                e.setCancelled(true);

                                shop.removeItem(sellingItem, false, false, null);

                                player.sendMessage(InternalUtil.CONFIGMANAGER.getBoughtMessage(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount(), shop.getPlayerName()));

                                Player pl = Bukkit.getPlayer(shop.getPlayerUUID());
                                String sellerMessage = InternalUtil.CONFIGMANAGER.getSellerMessage(
                                        sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount(), player.getName());

                                if (pl != null) {

                                    pl.sendMessage(sellerMessage);

                                } else {
                                    shop.addMessage(sellerMessage);

                                }
                            }

                        }

                    } else {
                        e.setCancelled(true);
                        player.sendMessage(InternalUtil.CONFIGMANAGER.getNoMoney());
                    }

                }

            }

        }

    }

}
