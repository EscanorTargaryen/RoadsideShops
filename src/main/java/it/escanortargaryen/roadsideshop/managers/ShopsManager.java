package it.escanortargaryen.roadsideshop.managers;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.events.PlayerBuyShopEvent;
import it.escanortargaryen.roadsideshop.inventory.ItemSettings;
import it.escanortargaryen.roadsideshop.inventory.SaleSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class ShopsManager implements Listener {

    public ShopsManager() {
        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);

    }

    public Shop getShop(InventoryHolder f) {

        for (Shop s : RoadsideShops.getCachedShops()) {

            if (s.getHolder().equals(f))
                return s;

        }
        return null;

    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if (getShop(e.getView().getTopInventory().getHolder()) == null)

            return;

        if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        Shop shop = getShop(e.getView().getTopInventory().getHolder());
        SellingItem sellingItem = shop.getItemAt(e.getSlot());
        if (e.getWhoClicked().getUniqueId().equals(shop.getPlayerUUID())) {

            if (e.getClickedInventory().getHolder() != (shop.getHolder())) {

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
                    e.getView().setCursor(new ItemStack(Material.AIR));

                    new SaleSettings(shop, i.clone(), (Player) e.getWhoClicked(), e.getSlot());

                }

            } else {

                new ItemSettings(shop, sellingItem, (Player) e.getWhoClicked());

            }

        } else {
            e.setCancelled(true);

            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

                if (sellingItem != null) {
                    if (RoadsideShops.getEconomy().has((OfflinePlayer) e.getWhoClicked(), sellingItem.getPrice())) {

                        PlayerBuyShopEvent ev = new PlayerBuyShopEvent(shop, sellingItem, (Player) e.getWhoClicked());

                        Bukkit.getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            Player p = (Player) e.getWhoClicked();

                            HashMap<Integer, ItemStack> i = p.getInventory().addItem(sellingItem.getItem());

                            if (i.size() > 0) {

                                p.sendMessage(InternalUtil.CONFIGMANAGER.getFullInvNoDrop());
                            } else {

                                RoadsideShops.getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), sellingItem.getPrice());
                                RoadsideShops.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(shop.getPlayerUUID()), sellingItem.getPrice());

                                e.setCancelled(true);

                                shop.removeItem(sellingItem, false, false, null);

                                e.getWhoClicked().sendMessage(InternalUtil.CONFIGMANAGER.getBoughtMessage(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount(), shop.getPlayerName()));

                                if (Bukkit.getPlayer(shop.getPlayerUUID()) != null) {

                                    Objects.requireNonNull(Bukkit.getPlayer(shop.getPlayerUUID())).sendMessage(InternalUtil.CONFIGMANAGER.getSellerMessage(
                                            sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount(), shop.getPlayerName()));

                                } else {
                                    shop.getOffMessages().add(InternalUtil.CONFIGMANAGER.getSellerMessage(
                                            sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount(), shop.getPlayerName()));

                                }
                            }

                        }

                    } else {
                        e.setCancelled(true);
                        e.getWhoClicked().sendMessage(InternalUtil.CONFIGMANAGER.getNoMoney());
                    }

                }

            }

        }

    }

}
