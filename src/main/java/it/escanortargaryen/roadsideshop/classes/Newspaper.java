package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class representing and managing the newspaper in the game. Currently it is not totally customizable, maybe it will be in the future.
 */
public class Newspaper implements Listener, InventoryHolder {

    /**
     * The list with all the sponsored items.
     */
    private ArrayList<SellingItem> items = new ArrayList<>();

    /**
     * The current number page.
     */
    private int page = 1;

    /**
     * Whether an animation is taking place.
     */
    private boolean duringAnimation = false;

    /**
     * Glass panel that is placed in the newspaper.
     */
    private final ItemStack glass;

    /**
     * Task that makes the animation run.
     */
    private BukkitTask animationTaskTimer;

    /**
     * Creates a new Newspaper.
     *
     * @param allShops All the recorded shops.
     * @param player   What player displays the newspaper.
     */
    public Newspaper(@NotNull Collection<Shop> allShops, @NotNull Player player) {
        Objects.requireNonNull(allShops);
        Objects.requireNonNull(player);

        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);

        glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = glass.getItemMeta();
        Objects.requireNonNull(m).setDisplayName(ChatColor.AQUA + "");
        glass.setItemMeta(m);

        ArrayList<SellingItem> t = new ArrayList<>();

        for (Shop s : allShops) {

            if (s.getSponsor() != null) {
                if (!s.getPlayerUUID().equals(player.getUniqueId()))

                    t.add(s.getSponsor());

            }

        }

        if (t.size() < 19) {

            items = t;
        } else {

            for (int i = 0; i < 18; i++) {

                int randomNum = ThreadLocalRandom.current().nextInt(0, t.size());
                items.add(t.get(randomNum));
                t.remove(randomNum);

            }

        }

        if (items.size() == 0) {
            player.sendMessage(InternalUtil.CONFIGMANAGER.getNoAdv());

        } else {

            player.openInventory(getInventory());
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, InternalUtil.CONFIGMANAGER.getNewspaperTitle());

        ItemStack paper = new ItemStack(Material.PAPER);

        for (int i = 0; i < 45; i++) {

            inv.setItem(i, glass.clone());

        }

        if (page == 1) {
            inv.setItem(26, InternalUtil.RIGHTARROW);

            inv.setItem(11, items.get(0).getForNewspaper());
            if (items.size() > 1)
                inv.setItem(20, items.get(1).getForNewspaper());
            else
                inv.setItem(20, paper);

            if (items.size() > 2)
                inv.setItem(29, items.get(2).getForNewspaper());
            else
                inv.setItem(29, paper);
            if (items.size() > 3)

                inv.setItem(15, items.get(3).getForNewspaper());
            else
                inv.setItem(15, paper);
            if (items.size() > 4)

                inv.setItem(24, items.get(4).getForNewspaper());
            else
                inv.setItem(24, paper);
            if (items.size() > 5)

                inv.setItem(33, items.get(5).getForNewspaper());
            else
                inv.setItem(33, paper);

        }
        if (page == 2) {

            inv.setItem(26, InternalUtil.RIGHTARROW);
            inv.setItem(18, InternalUtil.LEFTARROW);
            if (items.size() > 6)

                inv.setItem(11, items.get(6).getForNewspaper());
            else
                inv.setItem(11, paper);
            if (items.size() > 7)

                inv.setItem(20, items.get(7).getForNewspaper());
            else
                inv.setItem(20, paper);
            if (items.size() > 8)

                inv.setItem(29, items.get(8).getForNewspaper());
            else
                inv.setItem(29, paper);
            if (items.size() > 9)

                inv.setItem(15, items.get(9).getForNewspaper());
            else
                inv.setItem(15, paper);
            if (items.size() > 10)

                inv.setItem(24, items.get(10).getForNewspaper());
            else
                inv.setItem(24, paper);
            if (items.size() > 11)

                inv.setItem(33, items.get(11).getForNewspaper());
            else
                inv.setItem(33, paper);

        }

        if (page == 3) {

            inv.setItem(18, InternalUtil.LEFTARROW);
            if (items.size() > 12)

                inv.setItem(11, items.get(12).getForNewspaper());
            else
                inv.setItem(11, paper);
            if (items.size() > 13)

                inv.setItem(20, items.get(13).getForNewspaper());
            else
                inv.setItem(20, paper);
            if (items.size() > 14)

                inv.setItem(29, items.get(14).getForNewspaper());
            else
                inv.setItem(29, paper);
            if (items.size() > 15)

                inv.setItem(15, items.get(15).getForNewspaper());
            else
                inv.setItem(15, paper);
            if (items.size() > 16)

                inv.setItem(24, items.get(16).getForNewspaper());
            else
                inv.setItem(24, paper);
            if (items.size() > 17)

                inv.setItem(33, items.get(17).getForNewspaper());
            else
                inv.setItem(33, paper);

        }

        inv.setItem(13, new ItemStack(glass.clone()));
        inv.setItem(22, new ItemStack(glass.clone()));
        inv.setItem(31, new ItemStack(glass.clone()));

        inv.setItem(10, paper);
        inv.setItem(12, paper);
        inv.setItem(14, paper);
        inv.setItem(16, paper);
        inv.setItem(19, paper);
        inv.setItem(21, paper);
        inv.setItem(23, paper);
        inv.setItem(25, paper);
        inv.setItem(28, paper);
        inv.setItem(30, paper);
        inv.setItem(32, paper);
        inv.setItem(34, paper);

        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        Inventory topInventory = InternalUtil.getTopInventory(e);

        Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        if (topInventory.getHolder() != this)
            return;

        if (duringAnimation) {
            e.setCancelled(true);
            return;

        }

        e.setCancelled(true);

        if (Objects.requireNonNull(e.getInventory().getItem(e.getSlot())).getType() == Material.ARROW) {
            Inventory inv = InternalUtil.getTopInventory(e);

            if (e.getSlot() == 26) {

                page++;
                Inventory p = getInventory();
                duringAnimation = true;

                animationTaskTimer = new BukkitRunnable() {
                    int y = 0;

                    @Override
                    public void run() {

                        for (int i = 11; i < 17; i++) {

                            inv.setItem(i - 1,
                                    inv.getItem(i));

                        }

                        for (int i = 20; i < 26; i++) {

                            inv.setItem(i - 1,
                                    inv.getItem(i));

                        }

                        for (int i = 29; i < 35; i++) {

                            inv.setItem(i - 1,
                                    inv.getItem(i));

                        }
                        inv.setItem(16, p.getItem(10 + y));
                        inv.setItem(25, p.getItem(19 + y));
                        inv.setItem(34, p.getItem(28 + y));

                        y++;
                        if (y == 7) {

                            inv.setItem(26, p.getItem(26));
                            inv.setItem(18, p.getItem(18));

                            duringAnimation = false;
                            cancel();

                        }
                    }
                }.runTaskTimer(RoadsideShops.INSTANCE, 0, 3);

            }

            if (e.getSlot() == 18) {

                page--;
                Inventory p = getInventory();
                duringAnimation = true;
                animationTaskTimer = new BukkitRunnable() {
                    int y = 0;

                    @Override
                    public void run() {

                        for (int i = 15; i > 9; i--) {

                            inv.setItem(i + 1,
                                    inv.getItem(i));

                        }

                        for (int i = 24; i > 18; i--) {

                            inv.setItem(i + 1,
                                    inv.getItem(i));

                        }

                        for (int i = 33; i > 27; i--) {

                            inv.setItem(i + 1,
                                    inv.getItem(i));

                        }
                        inv.setItem(10, p.getItem(16 - y));
                        inv.setItem(19, p.getItem(25 - y));
                        inv.setItem(28, p.getItem(34 - y));

                        y++;
                        if (y == 7) {

                            inv.setItem(26, p.getItem(26));
                            inv.setItem(18, p.getItem(18));

                            duringAnimation = false;
                            cancel();

                        }
                    }
                }.runTaskTimer(RoadsideShops.INSTANCE, 0, 3);

            }

        }

        if (Objects.requireNonNull(e.getInventory().getItem(e.getSlot())).getType() != Material.PAPER) {

            int slot = e.getSlot();
            if (slot == 11) {

                if (page == 1)
                    dispatchShopCommand(player, items.get(0).getPlayerUUID());

                if (page == 2)
                    dispatchShopCommand(player, items.get(6).getPlayerUUID());

                if (page == 3)
                    dispatchShopCommand(player, items.get(12).getPlayerUUID());

            }
            if (slot == 20) {

                if (page == 1)
                    dispatchShopCommand(player, items.get(1).getPlayerUUID());

                if (page == 2)
                    dispatchShopCommand(player, items.get(7).getPlayerUUID());
                if (page == 3)
                    dispatchShopCommand(player, items.get(13).getPlayerUUID());

            }
            if (slot == 29) {
                if (page == 1)
                    dispatchShopCommand(player, items.get(2).getPlayerUUID());

                if (page == 2)
                    dispatchShopCommand(player, items.get(8).getPlayerUUID());
                if (page == 3)
                    dispatchShopCommand(player, items.get(14).getPlayerUUID());

            }
            if (slot == 15) {
                if (page == 1)
                    dispatchShopCommand(player, items.get(3).getPlayerUUID());

                if (page == 2)
                    dispatchShopCommand(player, items.get(9).getPlayerUUID());
                if (page == 3)
                    dispatchShopCommand(player, items.get(15).getPlayerUUID());

            }
            if (slot == 24) {
                if (page == 1)
                    dispatchShopCommand(player, items.get(4).getPlayerUUID());

                if (page == 2)
                    dispatchShopCommand(player, items.get(10).getPlayerUUID());
                if (page == 3)
                    dispatchShopCommand(player, items.get(16).getPlayerUUID());

            }
            if (slot == 33) {

                if (page == 1)
                    dispatchShopCommand(player, items.get(5).getPlayerUUID());
                if (page == 2)
                    dispatchShopCommand(player, items.get(1).getPlayerUUID());
                if (page == 3)
                    dispatchShopCommand(player, items.get(17).getPlayerUUID());

            }

        }

    }

    private void dispatchShopCommand(@NotNull HumanEntity humanEntity, @NotNull UUID owner) {
        Objects.requireNonNull(humanEntity);
        Objects.requireNonNull(owner);

        Bukkit.dispatchCommand(humanEntity,
                ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(owner).getName());

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (duringAnimation)
                animationTaskTimer.cancel();

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}
