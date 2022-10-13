package it.escanortargaryen.roadsideshop;

import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
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

    public static ConfigManager CONFIGMANAGER;
    public static ItemStack BACKARROW;

    public static ItemStack UNLOCKEDSLOT = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    public static ItemStack LOCKEDSLOT;
    public static ItemStack LOG;
    public static ItemStack RIGHTARROW, LEFTARROW;

    InternalUtil() {
        CONFIGMANAGER = new ConfigManager(RoadsideShops.INSTANCE);

        BACKARROW = new ItemStack(Material.ARROW);
        ItemMeta ws = BACKARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(InternalUtil.CONFIGMANAGER.getBackButtonTitle());
        ws.setLore(InternalUtil.CONFIGMANAGER.getBackButtonLore());
        BACKARROW.setItemMeta(ws);

        UNLOCKEDSLOT = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta h = UNLOCKEDSLOT.getItemMeta();
        Objects.requireNonNull(h).setDisplayName(CONFIGMANAGER.getUnlockedSlotPanelTitle()
        );

        h.setLore(CONFIGMANAGER.getUnlockedSlotPanelLore());
        UNLOCKEDSLOT.setItemMeta(h);

        LOCKEDSLOT = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta w = LOCKEDSLOT.getItemMeta();
        Objects.requireNonNull(w).setDisplayName(
                CONFIGMANAGER.getLockedSlotPanelTitle());

        w.setLore(CONFIGMANAGER.getLockedSlotPanelLore());
        LOCKEDSLOT.setItemMeta(w);

        LOG = new ItemStack(Material.OAK_LOG);
        ws = LOG.getItemMeta();
        Objects.requireNonNull(ws).setLore(List.of("§c§c§c§c§c§c§c§c§c§c§c§c"));
        Objects.requireNonNull(ws).setDisplayName(ChatColor.WHITE + "");
        LOG.setItemMeta(ws);

        RIGHTARROW = new ItemStack(Material.ARROW);
        ws = RIGHTARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(CONFIGMANAGER.getRightarrowTitle());
        ws.setLore(CONFIGMANAGER.getRightarrowLore());
        RIGHTARROW.setItemMeta(ws);

        LEFTARROW = new ItemStack(Material.ARROW);
        ws = LEFTARROW.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(CONFIGMANAGER.getLeftarrowTitle());
        ws.setLore(CONFIGMANAGER.getLeftarrowLore());
        LEFTARROW.setItemMeta(ws);

    }

    public static ItemStack generateMapItem(Shop shop, boolean isSponsoring, SellingItem sellingItem) {
        ItemStack sponsor;

        if (shop.getSponsor() != null && shop.getSponsor().equals(sellingItem)) {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());
            m.setLore(InternalUtil.CONFIGMANAGER.getSponsoredLore());
            sponsor.setItemMeta(m);
        } else if (shop.canSponsor(System.currentTimeMillis())) {

            if (isSponsoring) {

                sponsor = new ItemStack(Material.FILLED_MAP);
                ItemMeta m = sponsor.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(InternalUtil.CONFIGMANAGER.getSponsoringChange());
                } else {
                    m.setLore(InternalUtil.CONFIGMANAGER.getSponsoring());

                }
                sponsor.setItemMeta(m);

            } else {

                sponsor = new ItemStack(Material.PAPER);
                ItemMeta m = sponsor.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

                if (shop.getSponsor() != null) {
                    m.setLore(InternalUtil.CONFIGMANAGER.getNotSponsoringChange());
                } else {
                    m.setLore(InternalUtil.CONFIGMANAGER.getNotSponsoring());

                }
                sponsor.setItemMeta(m);

            }

        } else {
            sponsor = new ItemStack(Material.FILLED_MAP);
            ItemMeta m = sponsor.getItemMeta();
            Objects.requireNonNull(m).setDisplayName(InternalUtil.CONFIGMANAGER.getSponsorButtonTitle());

            m.setLore(InternalUtil.CONFIGMANAGER.getWaitToSponsor(shop.getMissTimeinMins(System.currentTimeMillis())));

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
