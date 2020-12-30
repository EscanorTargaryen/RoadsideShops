package it.davide.stands;

import java.util.*;

import net.commandcraft.invManagementPlugin.api.SafeInventoryActions;
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

import com.fren_gor.cmcSkyBlock.shop.SignUtilities;

import head.HeadLib;
import org.jetbrains.annotations.NotNull;

public class RemoveOSponsorIH implements InventoryHolder, Listener {

    Stand s;
    SellingItem i;

    public RemoveOSponsorIH(Stand s, SellingItem m, Player p) {

        Bukkit.getPluginManager().registerEvents(this, StandManager.getInstance());

        this.s = s;
        i = m;
        p.openInventory(getInventory());
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 27, ChatColor.DARK_BLUE + "Item Settings");

        ItemStack t = i.getWithpriceSeller().clone();
        ItemMeta tt = t.getItemMeta();
        List<String> a = tt.getLore();
        a.remove(a.size() - 1);
        a.remove(a.size() - 1);
        tt.setLore(a);
        t.setItemMeta(tt);

        inv.setItem(4, t);

        inv.setItem(20, HeadLib.WOODEN_ARROW_LEFT.toItemStack(ChatColor.BLUE + "Come back", "",
                ChatColor.GRAY + "Click to exit settings"));

        if (s.getSponsor() != null && s.getSponsor().equals(i)) {

            ItemStack sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            m.setDisplayName(ChatColor.GOLD + "Sposor item");
            ArrayList<String> ene = new ArrayList<>();
            ene.add("");
            ene.add(ChatColor.DARK_RED + "The item is just a sponsored item.");
            ene.add(ChatColor.DARK_RED + "Wait " + s.getMissTimeinMins(System.currentTimeMillis())
                    + " minutes to sponsor another item.");
            ene.add("");
            ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
            ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000) + " minutes.");
            m.setLore(ene);
            sponsor.setItemMeta(m);
            inv.setItem(22, sponsor);

        } else if (s.Checktime(System.currentTimeMillis())) {

            ItemStack sponsorItem = new ItemStack(Material.PAPER);
            ItemMeta m = sponsorItem.getItemMeta();
            m.setDisplayName(ChatColor.YELLOW + "Sponsor item");
            ArrayList<String> ene = new ArrayList<>();
            ene.add("");
            ene.add(ChatColor.RED + "The item isn't sponsored at the moment.");
            ene.add("");
            ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
            ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000) + " minutes.");
            ene.add("");
            if (s.getSponsor() != null) {

                ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                        + "You already have a sponsored item. Sponsoring");
                ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                ene.add("");

            }
            ene.add(ChatColor.GOLD + "Click to sponsor");
            m.setLore(ene);
            sponsorItem.setItemMeta(m);
            inv.setItem(22, sponsorItem);

        } else {
            ItemStack sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            m.setDisplayName(ChatColor.GOLD + "Sposor item");
            ArrayList<String> ene = new ArrayList<>();
            ene.add("");
            ene.add(ChatColor.DARK_RED + "You've already sponsored an item.");
            ene.add(ChatColor.DARK_RED + "Wait " + s.getMissTimeinMins(System.currentTimeMillis())
                    + " minutes to sponsor another item.");
            ene.add("");
            ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
            ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Stand.timesponsor / 60000) + " minutes.");
            m.setLore(ene);
            sponsor.setItemMeta(m);
            inv.setItem(22, sponsor);

        }

        ItemStack remove = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta rem = remove.getItemMeta();
        rem.setDisplayName(ChatColor.RED + "Remove Item");
        rem.setLore(Arrays.asList("", ChatColor.GOLD + "Click to remove the item and get it back"));
        remove.setItemMeta(rem);
        inv.setItem(24, remove);

        return inv;
    }

    boolean sponsor = false;

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
            }.runTask(StandManager.getInstance());

        }

        if (e.getSlot() == 24) {
            ItemStack here = StandManager.unlockedslot;

            Player p = (Player) e.getWhoClicked();

            switch (SafeInventoryActions.addItem(p.getInventory(), i.getI())) {

                case MODIFIED: {


                    s.getInvSeller().setItem(i.getSlot(), here);
                    s.getInvBuyer().setItem(i.getSlot(), new ItemStack(Material.AIR));

                    s.getItems().remove(i);

                    e.getWhoClicked().closeInventory();
                    if (s != null && s.getSponsor().equals(i)) {
                        s.setSponsor(null);

                    }

                    e.getWhoClicked()
                            .sendMessage(StandManager.configconfig.getString("remove-item").replace("&", "§")
                                    .replace("<price>", SignUtilities.formatVault(i.getPrice()))

                                    .replace("<type>", i.getI().getType().toString().toLowerCase().replace("_", " "))
                                    .replace("<amount>", i.getI().getAmount() + ""));

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "stand");

                        }
                    }.runTask(StandManager.getInstance());
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
            if (s.getSponsor() != null && s.getSponsor().equals(i))
                return;

            if (s.Checktime(System.currentTimeMillis())) {

                if (!this.sponsor) {
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
                    if (s.getSponsor() != null) {

                        ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                                + "You already have a sponsored item. Sponsoring");
                        ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                        ene.add("");

                    }
                    ene.add(ChatColor.GOLD + "Click to unsponsor");
                    m.setLore(ene);
                    sponsor.setItemMeta(m);
                    e.getInventory().setItem(22, sponsor);

                    this.sponsor = true;

                } else {

                    ItemStack sponsorItem = new ItemStack(Material.PAPER);
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
                    if (s.getSponsor() != null) {

                        ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                                + "You already have a sponsored item. Sponsoring");
                        ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                        ene.add("");

                    }
                    ene.add(ChatColor.GOLD + "Click to sponsor");
                    m.setLore(ene);
                    sponsorItem.setItemMeta(m);
                    e.getInventory().setItem(22, sponsorItem);

                    this.sponsor = false;
                }

            }

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            if (sponsor) {

                e.getPlayer()
                        .sendMessage(StandManager.configconfig.getString("sponsor-item-set").replace("&", "§")
                                .replace("<price>", SignUtilities.formatVault(i.getPrice()))
                                .replace("<type>", i.getI().getType().toString().toLowerCase().replace("_", " "))
                                .replace("<amount>", i.getI().getAmount() + ""));

                if (s.getSponsor() != null) {

                    s.getInvBuyer().setItem(s.getSponsor().getSlot(), s.getSponsor().getWithpriceBuyer());
                    s.getInvSeller().setItem(s.getSponsor().getSlot(), s.getSponsor().getWithpriceSeller());

                }

                s.setTimeSponsor(System.currentTimeMillis());
                s.setSponsor(i);

                s.getInvBuyer().setItem(i.getSlot(), i.getWithpriceESpondorBuyer());
                s.getInvSeller().setItem(i.getSlot(), i.getWithpriceESpondorSeller());

            }
            StandManager.getInstance().saveStand(s);

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}
