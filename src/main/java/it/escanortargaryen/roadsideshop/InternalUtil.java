package it.escanortargaryen.roadsideshop;

import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

/*

//TODO capire se serve il metodo calculate slots
//TODO metodo per sistemare i prezzi?
//TODO ci sono deprecate?
TODO cambia la scritta all'avvio sta scritto ancora standscore
TODO sistema il config, fai in modo che ci siano i metodi che danno già le strighe sistemate
TODO cose da sistemare in una classe:

        typo
        warnings
        spazi
        documentazione
        mettere private gli attributi
        rimuovere codice inutile e commenti
        rimuovere import inutili
        no parole in italiano*/
public class InternalUtil {

    public static ItemStack generateMapItem(Shop shop, boolean isSponsoring, SellingItem sellingItem) {
        ItemStack sponsor;

        if (shop.getSponsor() != null && shop.getSponsor().equals(sellingItem)) {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(ChatColor.GOLD + "Sponsor item");
            ArrayList<String> ene = new ArrayList<>();
            ene.add("");
            ene.add(ChatColor.DARK_RED + "The item is just a sponsored item.");
            ene.add(ChatColor.DARK_RED + "Wait " + shop.getMissTimeinMins(System.currentTimeMillis())
                    + " minutes to sponsor another item.");
            ene.add("");
            ene.add(ChatColor.GRAY + "Sponsoring an item shows it on the newspaper.");
            ene.add(ChatColor.GRAY + "You can sponsor an item every " + (Shop.timesponsor / 60000) + " minutes.");
            m.setLore(ene);
            sponsor.setItemMeta(m);
        } else if (shop.canSponsor(System.currentTimeMillis())) {

            if (isSponsoring) {

                sponsor = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = sponsor.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getSponsoringChange((Shop.timesponsor / 60000)));
                } else {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getSponsoring((Shop.timesponsor / 60000)));

                }
                sponsor.setItemMeta(m);

            } else {

                sponsor = new ItemStack(Material.PAPER);
                ItemMeta m = sponsor.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getNotSponsoringChange((Shop.timesponsor / 60000)));
                } else {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getNotSponsoring((Shop.timesponsor / 60000)));

                }
                sponsor.setItemMeta(m);

            }

        } else {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());

            m.setLore(RoadsideShops.CONFIGMANAGER.getWaitToSponsor((Shop.timesponsor / 60000), shop.getMissTimeinMins(System.currentTimeMillis())));

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
