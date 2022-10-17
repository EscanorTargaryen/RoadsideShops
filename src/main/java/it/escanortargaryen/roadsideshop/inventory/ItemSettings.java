package it.escanortargaryen.roadsideshop.inventory;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
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

import java.util.List;
import java.util.Objects;

public class ItemSettings implements InventoryHolder, Listener {

    private final Shop shop;
    private final SellingItem sellingItem;

    private boolean isSponsoring = false;

    public ItemSettings(@NotNull Shop shop, @NotNull SellingItem sellingItem, @NotNull Player player) {

        Objects.requireNonNull(shop);
        Objects.requireNonNull(sellingItem);
        Objects.requireNonNull(player);

        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);

        this.shop = shop;
        this.sellingItem = sellingItem;
        player.openInventory(getInventory());
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, InternalUtil.CONFIGMANAGER.getItemModify());

        ItemStack t = sellingItem.getWithPriceSeller().clone();
        ItemMeta tt = t.getItemMeta();
        List<String> a = Objects.requireNonNull(tt).getLore();
        Objects.requireNonNull(a).remove(a.size() - 1);
        a.remove(a.size() - 1);
        tt.setLore(a);
        t.setItemMeta(tt);

        inv.setItem(4, t);

        inv.setItem(20, InternalUtil.BACKARROW);

        inv.setItem(22, shop.generateMapItem(isSponsoring, sellingItem));

        ItemStack remove = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta rem = remove.getItemMeta();
        Objects.requireNonNull(rem).setDisplayName(InternalUtil.CONFIGMANAGER.getRemoveButtonTitle());
        rem.setLore(InternalUtil.CONFIGMANAGER.getRemoveButtonLore());
        remove.setItemMeta(rem);
        inv.setItem(24, remove);

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

        if (e.getSlot() == 20) {
            e.getWhoClicked().closeInventory();

            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.dispatchCommand(e.getWhoClicked(), ConfigManager.SHOPCOMMAND);

                }
            }.runTask(RoadsideShops.INSTANCE);

        }

        if (e.getSlot() == 24) {

            Player p = (Player) e.getWhoClicked();

            shop.removeItem(sellingItem, p);
            p.closeInventory();

            new BukkitRunnable() {

                @Override
                public void run() {

                    Bukkit.dispatchCommand(p, ConfigManager.SHOPCOMMAND);
                }
            }.runTask(RoadsideShops.INSTANCE);

        }

        if (e.getSlot() == 22) {
            this.isSponsoring = !this.isSponsoring;
            e.getInventory().setItem(22, shop.generateMapItem(isSponsoring, sellingItem));

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (isSponsoring) {

                e.getPlayer()
                        .sendMessage(InternalUtil.CONFIGMANAGER.getSponsorSet(sellingItem.getPrice(), sellingItem.getItem().getType().toString(), sellingItem.getItem().getAmount()));

                shop.setSponsor(sellingItem);
            }

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}
