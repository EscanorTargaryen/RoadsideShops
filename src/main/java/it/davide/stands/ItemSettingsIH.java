package it.davide.stands;

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

    private Stand stand;
    private ItemStack itemToSell;
    private int slotNumber;
    private boolean priceSet = false, isSponsoring = false, chiudi = false, exit = false;
    private ItemStack sponsorItem;

    private double price = 0.0;

    public ItemSettingsIH(Stand stand, ItemStack itemToSell, Player p, int slotNumber) {
        Bukkit.getPluginManager().registerEvents(this, StandManager.getInstance());

        this.stand = stand;
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

        if (stand.canSponsor(System.currentTimeMillis())) {
            if (isSponsoring) {

                ItemStack sponsor = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = sponsor.getItemMeta();
                m.setDisplayName(ChatColor.GOLD + "Sposor item");
                ArrayList<String> ene = new ArrayList<>();
                ene.add("");
                ene.add(ChatColor.GREEN + "The item will be sponsored.");
                ene.add("");
                ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
                ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000) + " minutes.");
                ene.add("");

                if (stand.getSponsor() != null) {

                    ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                            + "You already have a sponsored item. Sponsoring");
                    ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                    ene.add("");

                }
                ene.add(ChatColor.GOLD + "Click to unsponsor");
                m.setLore(ene);
                sponsor.setItemMeta(m);
                inv.setItem(15, sponsor);

            } else {

                sponsorItem = new ItemStack(Material.PAPER);
                ItemMeta m = sponsorItem.getItemMeta();
                m.setDisplayName(ChatColor.YELLOW + "Sponsor item");
                ArrayList<String> ene = new ArrayList<>();
                ene.add("");
                ene.add(ChatColor.RED + "The item isn't sponsored at the moment.");
                ene.add("");
                ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
                ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000) + " minutes.");
                ene.add("");
                if (stand.getSponsor() != null) {

                    ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                            + "You already have a sponsored item. Sponsoring");
                    ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                    ene.add("");

                }
                ene.add(ChatColor.GOLD + "Click to sponsor");
                m.setLore(ene);
                sponsorItem.setItemMeta(m);
                inv.setItem(15, sponsorItem);
            }

        } else {
            ItemStack sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            m.setDisplayName(ChatColor.GOLD + "Sposor item");
            ArrayList<String> ene = new ArrayList<>();
            ene.add("");
            ene.add(ChatColor.DARK_RED + "You've already sponsored an item.");
            ene.add(ChatColor.DARK_RED + "Wait " + stand.getMissTimeinMins(System.currentTimeMillis())
                    + " minutes to sponsor another item.");
            ene.add("");
            ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
            ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000) + " minutes.");
            m.setLore(ene);
            sponsor.setItemMeta(m);
            inv.setItem(15, sponsor);

        }

        if (priceSet) {

            ItemStack wool = new ItemStack(Material.GREEN_WOOL);
            ItemMeta mw = wool.getItemMeta();
            mw.setDisplayName(ChatColor.YELLOW + "Sell it");
            mw.setLore(Arrays.asList("", ChatColor.GOLD + "Click to finish up and sell the item"));
            wool.setItemMeta(mw);
            inv.setItem(24, wool);

            ItemStack prezzo = new ItemStack(Material.NAME_TAG);
            mw = prezzo.getItemMeta();
            mw.setDisplayName(ChatColor.translateAlternateColorCodes('&', StandManager.configconfig
                    .getString("price-message").replace("<value>", price + "")));

            mw.setLore(Arrays.asList("", ChatColor.GOLD + "Click to change the price"));
            prezzo.setItemMeta(mw);

            inv.setItem(6, prezzo);

        } else {

            ItemStack prezzo = new ItemStack(Material.NAME_TAG);
            ItemMeta mw = prezzo.getItemMeta();
            mw.setDisplayName(ChatColor.YELLOW + "Set a price");
            mw.setLore(Arrays.asList("", ChatColor.GOLD + "Click to set a price for this item"));
            prezzo.setItemMeta(mw);
            inv.setItem(6, prezzo);

            ItemStack wool = new ItemStack(Material.RED_WOOL);
            mw = wool.getItemMeta();
            mw.setDisplayName(ChatColor.YELLOW + "Sell the item");
            mw.setLore(Arrays.asList("", ChatColor.DARK_RED + "You must set a price before selling it"));
            wool.setItemMeta(mw);
            inv.setItem(24, wool);
        }

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
            chiudi = true;

            new AnvilGUI.Builder().onClose(player -> {

                        priceSet = true;
                        chiudi = false;
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

            if (stand.canSponsor(System.currentTimeMillis())) {

                if (!this.isSponsoring) {
                    ItemStack sponsor = new ItemStack(Material.FILLED_MAP);
                    ItemMeta m = sponsor.getItemMeta();
                    m.setDisplayName(ChatColor.GOLD + "Sposor item");
                    ArrayList<String> ene = new ArrayList<>();
                    ene.add("");
                    ene.add(ChatColor.GREEN + "The item will be sponsored.");
                    ene.add("");
                    ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
                    ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000)
                            + " minutes.");
                    ene.add("");
                    if (stand.getSponsor() != null) {

                        ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                                + "You already have a sponsored item. Sponsoring");
                        ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                        ene.add("");

                    }
                    ene.add(ChatColor.GOLD + "Click to unsponsor");
                    m.setLore(ene);
                    sponsor.setItemMeta(m);
                    e.getInventory().setItem(15, sponsor);

                    this.isSponsoring = true;

                } else {

                    sponsorItem = new ItemStack(Material.PAPER);
                    ItemMeta m = sponsorItem.getItemMeta();
                    m.setDisplayName(ChatColor.YELLOW + "Sponsor item");
                    ArrayList<String> ene = new ArrayList<>();
                    ene.add("");
                    ene.add(ChatColor.RED + "The item isn't sponsored at the moment.");
                    ene.add("");
                    ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
                    ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000)
                            + " minutes.");
                    ene.add("");
                    if (stand.getSponsor() != null) {

                        ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                                + "You already have a sponsored item. Sponsoring");
                        ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                        ene.add("");

                    }
                    ene.add(ChatColor.GOLD + "Click to sponsor");
                    m.setLore(ene);
                    sponsorItem.setItemMeta(m);
                    e.getInventory().setItem(15, sponsorItem);

                    this.isSponsoring = false;
                }

            }

        }
        if (e.getSlot() == 24) {

            if (priceSet) {
                chiudi = true;

                if (stand.getItems().size() == 0)
                    stand.getInvBuyer().setItem(1, new ItemStack(Material.AIR));
                SellingItem k = new SellingItem(itemToSell, slotNumber, price, stand.getP());
                stand.getItems().add(k);

                stand.getInvBuyer().setItem(k.getSlot(), k.getWithpriceBuyer());
                stand.getInvSeller().setItem(k.getSlot(), k.getWithpriceSeller());

                Player p = ((Player) e.getWhoClicked());
                p.closeInventory();
                Bukkit.dispatchCommand(p, "stand");
                exit = true;

                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        StandManager.configconfig.getString("put-item")
                                .replace("<price>", k.getPrice() + "")
                                .replace("<type>", k.getI().getType().toString().toLowerCase().replace("_", " "))
                                .replace("<amount>", k.getI().getAmount() + "")));

                if (isSponsoring) {

                    e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            StandManager.configconfig.getString("sponsor-set")));
                    if (stand.getSponsor() != null) {

                        stand.getInvBuyer().setItem(stand.getSponsor().getSlot(), stand.getSponsor().getWithpriceBuyer());
                        stand.getInvSeller().setItem(stand.getSponsor().getSlot(), stand.getSponsor().getWithpriceSeller());

                    }
                    stand.setTimeSponsor(System.currentTimeMillis());
                    stand.setSponsor(k);

                    stand.getInvBuyer().setItem(k.getSlot(), k.getWithpriceESpondorBuyer());
                    stand.getInvSeller().setItem(k.getSlot(), k.getWithpriceESpondorSeller());

                }

            }

        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (!chiudi) {
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
