package it.escanortargaryen.roadsideshop;

import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

/*

TODO capire se serve il metodo calculate slots
TODO metodo per sistemare i prezzi?
TODO sistema il config, fai in modo che ci siano i metodi che danno già le strighe sistemate
TODO fare in modo che si apre l'inventario degli stand quando si chide uno di settings
TODO fare in modo che si possa annullare l'inserimento del prezzo
/TODO metti il tempo in secondi
spostare qui le cose statiche interne
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

    public static ItemStack BACKARROW;
    InternalUtil(){
        BACKARROW= new ItemStack(Material.ARROW);
        ItemMeta ws = BACKARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(RoadsideShops.CONFIGMANAGER.getBackButtonTitle());
        ws.setLore(RoadsideShops.CONFIGMANAGER.getBackButtonLore());
        BACKARROW.setItemMeta(ws);
    }

    public static ItemStack generateMapItem(Shop shop, boolean isSponsoring, SellingItem sellingItem) {
        ItemStack sponsor;

        if (shop.getSponsor() != null && shop.getSponsor().equals(sellingItem)) {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());
            m.setLore(RoadsideShops.CONFIGMANAGER.getSponsoredLore());
            sponsor.setItemMeta(m);
        } else if (shop.canSponsor(System.currentTimeMillis())) {

            if (isSponsoring) {

                sponsor = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = sponsor.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getSponsoringChange());
                } else {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getSponsoring());

                }
                sponsor.setItemMeta(m);

            } else {

                sponsor = new ItemStack(Material.PAPER);
                ItemMeta m = sponsor.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getNotSponsoringChange());
                } else {
                    m.setLore(RoadsideShops.CONFIGMANAGER.getNotSponsoring());

                }
                sponsor.setItemMeta(m);

            }

        } else {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(RoadsideShops.CONFIGMANAGER.getSponsorButtonTitle());

            m.setLore(RoadsideShops.CONFIGMANAGER.getWaitToSponsor(shop.getMissTimeinMins(System.currentTimeMillis())));

            sponsor.setItemMeta(m);

        }
        return sponsor.clone();
    }

    public static void setSponsorItem(Shop shop, SellingItem sellingItem) {
        if (shop.getSponsor() != null) {

            shop.getInvBuyer().setItem(shop.getSponsor().getSlot(), shop.getSponsor().getWithPriceBuyer());
            shop.getInvSeller().setItem(shop.getSponsor().getSlot(), shop.getSponsor().getWithPriceSeller());

        }
        shop.setTimeSponsor(System.currentTimeMillis());
        shop.setSponsor(sellingItem);

        shop.getInvBuyer().setItem(sellingItem.getSlot(), sellingItem.getWithPriceAndSponsorBuyer());
        shop.getInvSeller().setItem(sellingItem.getSlot(), sellingItem.getWithPriceAndSponsorSeller());

    }

}
