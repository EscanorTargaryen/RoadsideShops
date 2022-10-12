package it.escanortargaryen.roadsideshop.classes;

import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

//TODO make newspaper number pages customizable
public class Newspaper implements Listener, InventoryHolder {

    private ArrayList<SellingItem> list = new ArrayList<>();
    private int pagina = 1;

    private boolean animation = false;
    private final ItemStack glass;

    public Newspaper(Collection<Shop> collection, Player p) {
        Bukkit.getPluginManager().registerEvents(this, RoadsideShops.INSTANCE);

        glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = glass.getItemMeta();
        Objects.requireNonNull(m).setDisplayName(ChatColor.AQUA + "");
        glass.setItemMeta(m);

        ArrayList<SellingItem> sel = new ArrayList<>();

        for (Shop s : collection) {

            if (s.getSponsor() != null) {
                if (!s.getPlayerUUID().equals(p.getUniqueId()))

                    sel.add(s.getSponsor());

            }

        }

        if (sel.size() < 19) {

            list = sel;
        } else {

            for (int i = 0; i < 18; i++) {

                int randomNum = ThreadLocalRandom.current().nextInt(0, sel.size());
                list.add(sel.get(randomNum));
                sel.remove(randomNum);

            }

        }

        if (list.size() == 0) {
            p.sendMessage(RoadsideShops.CONFIGMANAGER.getNoAdv());

        } else {

            p.openInventory(getInventory());
        }
    }

    BukkitTask task;

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, RoadsideShops.CONFIGMANAGER.getNewspaperTitle());

        ItemStack paper = new ItemStack(Material.PAPER);

        for (int i = 0; i < 45; i++) {

            inv.setItem(i, glass.clone());

        }

        if (pagina == 1) {
            inv.setItem(26, RoadsideShops.RIGHTARROW);

            inv.setItem(11, list.get(0).getForNewspaper());
            if (list.size() > 1)
                inv.setItem(20, list.get(1).getForNewspaper());
            else
                inv.setItem(20, paper);

            if (list.size() > 2)
                inv.setItem(29, list.get(2).getForNewspaper());
            else
                inv.setItem(29, paper);
            if (list.size() > 3)

                inv.setItem(15, list.get(3).getForNewspaper());
            else
                inv.setItem(15, paper);
            if (list.size() > 4)

                inv.setItem(24, list.get(4).getForNewspaper());
            else
                inv.setItem(24, paper);
            if (list.size() > 5)

                inv.setItem(33, list.get(5).getForNewspaper());
            else
                inv.setItem(33, paper);

        }
        if (pagina == 2) {

            inv.setItem(26, RoadsideShops.RIGHTARROW);
            inv.setItem(18, RoadsideShops.LEFTARROW);
            if (list.size() > 6)

                inv.setItem(11, list.get(6).getForNewspaper());
            else
                inv.setItem(11, paper);
            if (list.size() > 7)

                inv.setItem(20, list.get(7).getForNewspaper());
            else
                inv.setItem(20, paper);
            if (list.size() > 8)

                inv.setItem(29, list.get(8).getForNewspaper());
            else
                inv.setItem(29, paper);
            if (list.size() > 9)

                inv.setItem(15, list.get(9).getForNewspaper());
            else
                inv.setItem(15, paper);
            if (list.size() > 10)

                inv.setItem(24, list.get(10).getForNewspaper());
            else
                inv.setItem(24, paper);
            if (list.size() > 11)

                inv.setItem(33, list.get(11).getForNewspaper());
            else
                inv.setItem(33, paper);

        }

        if (pagina == 3) {

            inv.setItem(18, RoadsideShops.LEFTARROW);
            if (list.size() > 12)

                inv.setItem(11, list.get(12).getForNewspaper());
            else
                inv.setItem(11, paper);
            if (list.size() > 13)

                inv.setItem(20, list.get(13).getForNewspaper());
            else
                inv.setItem(20, paper);
            if (list.size() > 14)

                inv.setItem(29, list.get(14).getForNewspaper());
            else
                inv.setItem(29, paper);
            if (list.size() > 15)

                inv.setItem(15, list.get(15).getForNewspaper());
            else
                inv.setItem(15, paper);
            if (list.size() > 16)

                inv.setItem(24, list.get(16).getForNewspaper());
            else
                inv.setItem(24, paper);
            if (list.size() > 17)

                inv.setItem(33, list.get(17).getForNewspaper());
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

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        if (e.getView().getTopInventory().getHolder() != this)
            return;

        if (animation) {
            e.setCancelled(true);
            return;

        }

        e.setCancelled(true);

        if (Objects.requireNonNull(e.getInventory().getItem(e.getSlot())).getType() == Material.ARROW) {

            if (e.getSlot() == 26) {

                pagina++;
                Inventory p = getInventory();
                animation = true;

                task = new BukkitRunnable() {
                    int y = 0;

                    @Override
                    public void run() {

                        for (int i = 11; i < 17; i++) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(i - 1,
                                    e.getWhoClicked().getOpenInventory().getTopInventory().getItem(i));

                        }

                        for (int i = 20; i < 26; i++) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(i - 1,
                                    e.getWhoClicked().getOpenInventory().getTopInventory().getItem(i));

                        }

                        for (int i = 29; i < 35; i++) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(i - 1,
                                    e.getWhoClicked().getOpenInventory().getTopInventory().getItem(i));

                        }
                        e.getWhoClicked().getOpenInventory().getTopInventory().setItem(16, p.getItem(10 + y));
                        e.getWhoClicked().getOpenInventory().getTopInventory().setItem(25, p.getItem(19 + y));
                        e.getWhoClicked().getOpenInventory().getTopInventory().setItem(34, p.getItem(28 + y));

                        y++;
                        if (y == 7) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(26, p.getItem(26));
                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(18, p.getItem(18));

                            animation = false;
                            cancel();

                        }
                    }
                }.runTaskTimer(RoadsideShops.INSTANCE, 0, 3);

            }

            if (e.getSlot() == 18) {

                pagina--;
                Inventory p = getInventory();
                animation = true;
                task = new BukkitRunnable() {
                    int y = 0;

                    @Override
                    public void run() {

                        for (int i = 15; i > 9; i--) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(i + 1,
                                    e.getWhoClicked().getOpenInventory().getTopInventory().getItem(i));

                        }

                        for (int i = 24; i > 18; i--) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(i + 1,
                                    e.getWhoClicked().getOpenInventory().getTopInventory().getItem(i));

                        }

                        for (int i = 33; i > 27; i--) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(i + 1,
                                    e.getWhoClicked().getOpenInventory().getTopInventory().getItem(i));

                        }
                        e.getWhoClicked().getOpenInventory().getTopInventory().setItem(10, p.getItem(16 - y));
                        e.getWhoClicked().getOpenInventory().getTopInventory().setItem(19, p.getItem(25 - y));
                        e.getWhoClicked().getOpenInventory().getTopInventory().setItem(28, p.getItem(34 - y));

                        y++;
                        if (y == 7) {

                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(26, p.getItem(26));
                            e.getWhoClicked().getOpenInventory().getTopInventory().setItem(18, p.getItem(18));

                            animation = false;
                            cancel();

                        }
                    }
                }.runTaskTimer(RoadsideShops.INSTANCE, 0, 3);

            }

        }

        if (Objects.requireNonNull(e.getInventory().getItem(e.getSlot())).getType() != Material.PAPER) {

            if (e.getSlot() == 11) {

                if (pagina == 1)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(0).getPlayerUUID()).getName());

                if (pagina == 2)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(6).getPlayerUUID()).getName());
                if (pagina == 3)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(12).getPlayerUUID()).getName());

            }
            if (e.getSlot() == 20) {

                if (pagina == 1)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(1).getPlayerUUID()).getName());

                if (pagina == 2)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(7).getPlayerUUID()).getName());
                if (pagina == 3)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(13).getPlayerUUID()).getName());

            }
            if (e.getSlot() == 29) {
                if (pagina == 1)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(2).getPlayerUUID()).getName());

                if (pagina == 2)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(8).getPlayerUUID()).getName());
                if (pagina == 3)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(14).getPlayerUUID()).getName());

            }
            if (e.getSlot() == 15) {
                if (pagina == 1)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(3).getPlayerUUID()).getName());

                if (pagina == 2)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(9).getPlayerUUID()).getName());
                if (pagina == 3)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(15).getPlayerUUID()).getName());

            }
            if (e.getSlot() == 24) {
                if (pagina == 1)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(4).getPlayerUUID()).getName());

                if (pagina == 2)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(10).getPlayerUUID()).getName());
                if (pagina == 3)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(16).getPlayerUUID()).getName());

            }
            if (e.getSlot() == 33) {

                if (pagina == 1)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(5).getPlayerUUID()).getName());

                if (pagina == 2)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(11).getPlayerUUID()).getName());
                if (pagina == 3)
                    Bukkit.dispatchCommand(e.getWhoClicked(),
                            ConfigManager.SHOPCOMMAND + " " + Bukkit.getOfflinePlayer(list.get(17).getPlayerUUID()).getName());

            }

        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (animation)
                task.cancel();

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}
