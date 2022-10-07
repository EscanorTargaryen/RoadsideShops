package it.escanortargaryen.roadsideshop;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/*

//TODO capire se serve il metodo calculate slots
//TODO metodo per sistemare i prezzi?
//TODO ci sono deprecate?

TODO cose da sistemare in una classe:

        typo
        warnings
        spazi
        documentazione
        mettere private gli attributi
        rimuovere codice inutile e commenti
        rimuovere import inutili
        no parole in italiano*/
class InternalUtil {

    public static ItemStack generateMapItem(Shop shop, boolean isSponsoring) {
        ItemStack sponsor;
        if (shop.canSponsor(System.currentTimeMillis())) {
            if (isSponsoring) {

                sponsor = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = sponsor.getItemMeta();
                m.setDisplayName(ChatColor.GOLD + "Sposor item");
                ArrayList<String> ene = new ArrayList<>();
                ene.add("");
                ene.add(ChatColor.GREEN + "The item will be sponsored.");
                ene.add("");
                ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
                ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Shop.timesponsor / 60000) + " minutes.");
                ene.add("");

                if (shop.getSponsor() != null) {

                    ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                            + "You already have a sponsored item. Sponsoring");
                    ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                    ene.add("");

                }
                ene.add(ChatColor.GOLD + "Click to unsponsor");
                m.setLore(ene);
                sponsor.setItemMeta(m);

            } else {

                sponsor = new ItemStack(Material.PAPER);
                ItemMeta m = sponsor.getItemMeta();
                m.setDisplayName(ChatColor.YELLOW + "Sponsor item");
                ArrayList<String> ene = new ArrayList<>();
                ene.add("");
                ene.add(ChatColor.RED + "The item isn't sponsored at the moment.");
                ene.add("");
                ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
                ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Shop.timesponsor / 60000) + " minutes.");
                ene.add("");
                if (shop.getSponsor() != null) {

                    ene.add(ChatColor.DARK_RED + "N.B.: " + ChatColor.RED
                            + "You already have a sponsored item. Sponsoring");
                    ene.add(ChatColor.RED + "this item is going to unsponsor the other one.");
                    ene.add("");

                }
                ene.add(ChatColor.GOLD + "Click to sponsor");
                m.setLore(ene);
                sponsor.setItemMeta(m);

            }

        } else {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            m.setDisplayName(ChatColor.GOLD + "Sposor item");
            ArrayList<String> ene = new ArrayList<>();
            ene.add("");
            ene.add(ChatColor.DARK_RED + "You've already sponsored an item.");
            ene.add(ChatColor.DARK_RED + "Wait " + shop.getMissTimeinMins(System.currentTimeMillis())
                    + " minutes to sponsor another item.");
            ene.add("");
            ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
            ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Shop.timesponsor / 60000) + " minutes.");
            m.setLore(ene);
            sponsor.setItemMeta(m);

        }
        return sponsor.clone();
    }

    public static void setSponsorItem(Shop shop, SellingItem sellingItem) {
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
