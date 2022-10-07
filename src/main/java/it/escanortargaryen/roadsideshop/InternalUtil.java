package it.escanortargaryen.roadsideshop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*

//TODO capire se serve il metodo calculate slots
//TODO metodo per sistemare i prezzi?
//TODO ci sono deprecate?
TODO cambia la scritta all'avvio sta scritto ancora standscore
sistema il config, fai in modo che ci siano i metodi che danno già le strighe sistemate
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
                m.setDisplayName(StandManager.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(StandManager.CONFIGMANAGER.getSponsoringChange((Shop.timesponsor / 60000)));
                } else {
                    m.setLore(StandManager.CONFIGMANAGER.getSponsoring((Shop.timesponsor / 60000)));

                }
                sponsor.setItemMeta(m);

            } else {

                sponsor = new ItemStack(Material.PAPER);
                ItemMeta m = sponsor.getItemMeta();
                m.setDisplayName(StandManager.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(StandManager.CONFIGMANAGER.getNotSponsoringChange((Shop.timesponsor / 60000)));
                } else {
                    m.setLore(StandManager.CONFIGMANAGER.getNotSponsoring((Shop.timesponsor / 60000)));

                }
                sponsor.setItemMeta(m);

            }

        } else {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            m.setDisplayName(StandManager.CONFIGMANAGER.getSponsorButtonTitle());

            m.setLore(StandManager.CONFIGMANAGER.getWaitToSponsor((Shop.timesponsor / 60000), shop.getMissTimeinMins(System.currentTimeMillis())));

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
