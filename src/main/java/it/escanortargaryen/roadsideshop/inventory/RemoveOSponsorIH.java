package it.escanortargaryen.roadsideshop.inventory;

import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import de.erethon.headlib.HeadLib;
import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RemoveOSponsorIH implements InventoryHolder, Listener {

    private final Shop shop;
    private final SellingItem sellingItem;

    private boolean isSponsoring = false;

    public RemoveOSponsorIH(Shop shop, SellingItem sellingItem, Player p) {

        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.getInstance());

        this.shop = shop;
        this.sellingItem = sellingItem;
        p.openInventory(getInventory());
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, ChatColor.DARK_BLUE + "Item Settings");

        ItemStack t = sellingItem.getWithpriceSeller().clone();
        ItemMeta tt = t.getItemMeta();
        List<String> a = Objects.requireNonNull(tt).getLore();
        Objects.requireNonNull(a).remove(a.size() - 1);
        a.remove(a.size() - 1);
        tt.setLore(a);
        t.setItemMeta(tt);

        inv.setItem(4, t);

        inv.setItem(20, HeadLib.WOODEN_ARROW_LEFT.toItemStack(ChatColor.BLUE + "Come back", "",
                ChatColor.GRAY + "Click to exit settings"));

        inv.setItem(22, InternalUtil.generateMapItem(shop, isSponsoring, sellingItem));

        ItemStack remove = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta rem = remove.getItemMeta();
        Objects.requireNonNull(rem).setDisplayName(ChatColor.RED + "Remove Item");
        rem.setLore(Arrays.asList("", ChatColor.GOLD + "Click to remove the item and get it back"));
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
                    Bukkit.dispatchCommand(e.getWhoClicked(), "stand");

                }
            }.runTask(RoadsideShops.getInstance());

        }

        if (e.getSlot() == 24) {
            ItemStack here = RoadsideShops.unlockedslot;

            Player p = (Player) e.getWhoClicked();

            switch (SafeInventoryActions.addItem(p.getInventory(), sellingItem.getI())) {

                case MODIFIED: {

                    shop.getInvSeller().setItem(sellingItem.getSlot(), here);
                    shop.getInvBuyer().setItem(sellingItem.getSlot(), new ItemStack(Material.AIR));

                    shop.getItems().remove(sellingItem);

                    e.getWhoClicked().closeInventory();
                    if (shop.getSponsor().equals(sellingItem)) {
                        shop.setSponsor(null);

                    }

                    e.getWhoClicked()
                            .sendMessage(RoadsideShops.CONFIGMANAGER.getRemoveItem().replace("&", "§")
                                    .replace("<price>", sellingItem.getPrice() + "")

                                    .replace("<type>", sellingItem.getI().getType().toString().toLowerCase().replace("_", " "))
                                    .replace("<amount>", sellingItem.getI().getAmount() + ""));

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "stand");

                        }
                    }.runTask(RoadsideShops.getInstance());
                    break;
                }

                case NOT_ENOUGH_SPACE:
                case NOT_MODIFIED: {

                    p.sendMessage(ChatColor.RED + "You can't remove item from the stand: inventory full");

                    break;
                }

            }

        }

        if (e.getSlot() == 22) {

            e.getInventory().setItem(22, InternalUtil.generateMapItem(shop, isSponsoring, sellingItem));

            this.isSponsoring = !this.isSponsoring;

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (isSponsoring) {

                e.getPlayer()
                        .sendMessage(RoadsideShops.CONFIGMANAGER.getSponsorItemSet().replace("&", "§")
                                .replace("<price>", sellingItem.getPrice() + "")
                                .replace("<type>", sellingItem.getI().getType().toString().toLowerCase().replace("_", " "))
                                .replace("<amount>", sellingItem.getI().getAmount() + ""));

                InternalUtil.setSponsorItem(shop, sellingItem);
            }

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}
