package it.escanortargaryen.roadsideshop.managers;

import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.events.PlayerBuyShopEvent;
import it.escanortargaryen.roadsideshop.inventory.SaleSettings;
import it.escanortargaryen.roadsideshop.inventory.ItemSettings;
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
import org.bukkit.scheduler.BukkitRunnable;

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

        e.getView().getTopInventory();
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

        if (e.getWhoClicked().getUniqueId().equals(shop.getPlayerUUID())) {

            if (e.getClickedInventory().getHolder() != (shop.getHolder())) {

                if (e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT
                        || e.getClick() == ClickType.SHIFT_RIGHT) {

                    e.setCancelled(true);

                }

                return;
            }

            if (shop.getItemAt(e.getSlot()) == null) {
                e.setCancelled(true);

                if (!shop.canSell(e.getSlot()))
                    return;

                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

                    return;

                }

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        ItemStack i = e.getCursor().clone();
                        e.getView().setCursor(new ItemStack(Material.AIR));

                        new SaleSettings(shop, i.clone(), (Player) e.getWhoClicked(), e.getSlot());

                    }
                }.runTaskLater(RoadsideShops.INSTANCE, 2);

            } else {

                e.setCancelled(true);
                new ItemSettings(shop, shop.getItemAt(e.getSlot()), (Player) e.getWhoClicked());

            }

        } else {
            e.setCancelled(true);

            SellingItem venduto = null;

            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

                for (SellingItem c : shop.getItems()) {

                    if (c.getSlot() == e.getSlot()) {

                        if (RoadsideShops.getEconomy().has((OfflinePlayer) e.getWhoClicked(), c.getPrice())) {

                            PlayerBuyShopEvent ev = new PlayerBuyShopEvent(shop, c, (Player) e.getWhoClicked());

                            Bukkit.getPluginManager().callEvent(ev);

                            if (!ev.isCancelled()) {
                                Player p = (Player) e.getWhoClicked();
                                switch (SafeInventoryActions.addItem(p.getInventory(), c.getI())) {

                                    case MODIFIED: {

                                        RoadsideShops.getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), c.getPrice());
                                        RoadsideShops.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(shop.getPlayerUUID()), c.getPrice());
                                        venduto = c;
                                        e.setCancelled(true);

                                        if (c.equals(shop.getSponsor())) {
                                            shop.getInvBuyer().setItem(shop.getSponsor().getSlot(),
                                                    shop.getSponsor().getWithPriceBuyer());
                                            shop.getInvSeller().setItem(shop.getSponsor().getSlot(),
                                                    shop.getSponsor().getWithPriceSeller());

                                            shop.setSponsor(null);

                                        }

                                        e.getWhoClicked().sendMessage(InternalUtil.CONFIGMANAGER.getBoughtMessage(c.getPrice(), c.getI().getType().toString(), c.getI().getAmount(), shop.getPlayerName()));

                                        if (Bukkit.getPlayer(shop.getPlayerUUID()) != null) {

                                            Objects.requireNonNull(Bukkit.getPlayer(shop.getPlayerUUID())).sendMessage(InternalUtil.CONFIGMANAGER.getSellerMessage(
                                                    c.getPrice(), c.getI().getType().toString(), c.getI().getAmount(), shop.getPlayerName()));

                                        } else {
                                            shop.getOffMessages().add(InternalUtil.CONFIGMANAGER.getSellerMessage(
                                                    c.getPrice(), c.getI().getType().toString(), c.getI().getAmount(), shop.getPlayerName()));

                                        }
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {

                                                shop.getInvSeller().setItem(c.getSlot(), new ItemStack(InternalUtil.UNLOCKEDSLOT));

                                                shop.getInvBuyer().setItem(c.getSlot(), new ItemStack(Material.AIR));

                                            }
                                        }.runTaskLater(RoadsideShops.INSTANCE, 2);
                                        break;
                                    }

                                    case NOT_MODIFIED:
                                    case NOT_ENOUGH_SPACE: {

                                        p.sendMessage(InternalUtil.CONFIGMANAGER.getFullInv());

                                        break;
                                    }

                                }

                            }

                        } else {
                            e.setCancelled(true);
                            e.getWhoClicked().sendMessage(InternalUtil.CONFIGMANAGER.getNoMoney());
                        }

                    }
                }
                if (venduto != null) {

                    shop.getItems().remove(venduto);

                }

            } else {
                e.setCancelled(true);

            }

        }

    }

}
