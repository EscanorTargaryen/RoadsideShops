package it.escanortargaryen.roadsideshop.managers;

import it.escanortargaryen.roadsideshop.RoadsideShops;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.naming.ConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {

    private final YamlConfiguration config;

    private String priceMessage;
    private String unlockedSlotPanelTitle;
    private List<String> unlockedSlotPanelLore;
    private String lockedSlotPanelTitle;
    private List<String> lockedSlotPanelLore;
    private String putItem;
    private String sponsorSet;
    private String noAdv;
    private String removeItem;
    private String boughtMessage;
    private String sellerMessage;
    public static long SPONSORTIME;

    private String sponsorButtonTitle;

    private List<String> sponsoring, sponsoringChange, notSponsoring, notSponsoringChange, waitToSponsor;

    private String leftarrowTitle, rightarrowTitle, newspaperTitle;

    private List<String> leftarrowLore, rightarrowLore, itemSaleSeller, itemSaleBuyer, itemSaleSellerSponsor, itemSaleBuyerSponsor, loreForNewspaper;

    public ConfigManager(Plugin pl) {

        final File c = new File(pl.getDataFolder() + "/config.yml");
        config = YamlConfiguration.loadConfiguration(c);
        try {
            loadAndValidateConfig();
        } catch (ConfigurationException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            RoadsideShops.INSTANCE.getServer().getPluginManager().disablePlugin(RoadsideShops.INSTANCE);
        }
    }

    public void loadAndValidateConfig() throws ConfigurationException {

        String notSet = "is not set in config.yml";

        if (!config.isSet("price-message")) {
            throw new ConfigurationException("price-message " + notSet);
        }
        priceMessage = config.getString("price-message");

        if (!config.isSet("unlocked-slot-panel-title")) {
            throw new ConfigurationException("unlocked-slot-panel-title " + notSet);
        }
        unlockedSlotPanelTitle = config.getString("unlocked-slot-panel-title");

        if (!config.isSet("unlocked-slot-lore")) {
            throw new ConfigurationException("unlocked-slot-lore " + notSet);
        }
        unlockedSlotPanelLore = config.getStringList("unlocked-slot-lore");

        if (!config.isSet("locked-slot-panel-title")) {
            throw new ConfigurationException("locked-slot-panel-title " + notSet);
        }
        lockedSlotPanelTitle = config.getString("locked-slot-panel-title");

        if (!config.isSet("locked-slot-lore")) {
            throw new ConfigurationException("locked-slot-lore " + notSet);
        }
        lockedSlotPanelLore = config.getStringList("locked-slot-lore");

        if (!config.isSet("put-item")) {
            throw new ConfigurationException("put-item " + notSet);
        }
        putItem = config.getString("put-item");

        if (!config.isSet("sponsor-set")) {
            throw new ConfigurationException("sponsor-set " + notSet);
        }
        sponsorSet = config.getString("sponsor-set");

        if (!config.isSet("no-adv")) {
            throw new ConfigurationException("no-adv " + notSet);
        }
        noAdv = config.getString("no-adv");

        if (!config.isSet("remove-item")) {
            throw new ConfigurationException("remove-item " + notSet);
        }
        removeItem = config.getString("remove-item");

        if (!config.isSet("bought-message")) {
            throw new ConfigurationException("bought-message " + notSet);
        }
        boughtMessage = config.getString("bought-message");

        if (!config.isSet("seller-message")) {
            throw new ConfigurationException("seller-message " + notSet);
        }
        sellerMessage = config.getString("seller-message");

        if (!config.isSet("sponsor-time")) {
            throw new ConfigurationException("sponsor-time " + notSet);
        }
        SPONSORTIME = config.getLong("sponsor-time");
        if (SPONSORTIME < 0) throw new ConfigurationException("sponsor-time could not be negative");

        if (!config.isSet("sponsor-button.title")) {
            throw new ConfigurationException("sponsor-button.title " + notSet);
        }
        sponsorButtonTitle = config.getString("sponsor-button.title");

        if (!config.isSet("sponsor-button.sponsoring")) {
            throw new ConfigurationException("sponsor-button.sponsoring " + notSet);
        }
        sponsoring = config.getStringList("sponsor-button.sponsoring");

        if (!config.isSet("sponsor-button.sponsoring-change")) {
            throw new ConfigurationException("sponsor-button.sponsoring-change " + notSet);
        }
        sponsoringChange = config.getStringList("sponsor-button.sponsoring-change");

        if (!config.isSet("sponsor-button.not-sponsoring")) {
            throw new ConfigurationException("sponsor-button.not-sponsoring " + notSet);
        }
        notSponsoring = config.getStringList("sponsor-button.not-sponsoring");

        if (!config.isSet("sponsor-button.not-sponsoring-change")) {
            throw new ConfigurationException("sponsor-button.not-sponsoring-change " + notSet);
        }
        notSponsoringChange = config.getStringList("sponsor-button.not-sponsoring-change");

        if (!config.isSet("sponsor-button.wait")) {
            throw new ConfigurationException("sponsor-button.wait " + notSet);
        }
        waitToSponsor = config.getStringList("sponsor-button.wait");

        if (!config.isSet("newspaper.arrows.right.lore")) {
            throw new ConfigurationException("newspaper.arrows.right.lore " + notSet);
        }
        rightarrowLore = config.getStringList("newspaper.arrows.right.lore");

        if (!config.isSet("newspaper.arrows.left.lore")) {
            throw new ConfigurationException("newspaper.arrows.left.lore " + notSet);
        }
        leftarrowLore = config.getStringList("newspaper.arrows.left.lore");

        if (!config.isSet("newspaper.arrows.left.title")) {
            throw new ConfigurationException("newspaper.arrows.left.title " + notSet);
        }
        leftarrowTitle = config.getString("newspaper.arrows.left.title");

        if (!config.isSet("newspaper.arrows.right.title")) {
            throw new ConfigurationException("newspaper.arrows.right.title " + notSet);
        }
        rightarrowTitle = config.getString("newspaper.arrows.right.title");

        if (!config.isSet("newspaper.title")) {
            throw new ConfigurationException("newspaper.title " + notSet);
        }
        newspaperTitle = config.getString("newspaper.title");

        if (!config.isSet("item-sale-lore.seller")) {
            throw new ConfigurationException("item-sale-lore.seller " + notSet);
        }
        itemSaleSeller = config.getStringList("item-sale-lore.seller");

        if (!config.isSet("item-sale-lore.buyer")) {
            throw new ConfigurationException("item-sale-lore.buyer " + notSet);
        }
        itemSaleBuyer = config.getStringList("item-sale-lore.buyer");

        if (!config.isSet("item-sale-lore.seller-sponsor")) {
            throw new ConfigurationException("item-sale-lore.seller-sponsor " + notSet);
        }
        itemSaleSellerSponsor = config.getStringList("item-sale-lore.seller-sponsor");

        if (!config.isSet("item-sale-lore.buyer-sponsor")) {
            throw new ConfigurationException("item-sale-lore.buyer-sponsor " + notSet);
        }
        itemSaleBuyerSponsor = config.getStringList("item-sale-lore.buyer-sponsor");

        if (!config.isSet("item-sale-lore.newspaper")) {
            throw new ConfigurationException("item-sale-lore.newspaper " + notSet);
        }
        loreForNewspaper = config.getStringList("item-sale-lore.newspaper");

    }

    public List<String> getItemSaleBuyer(double price) {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : itemSaleBuyer) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return ino;
    }

    public List<String> getItemSaleBuyerSponsor(double price) {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : itemSaleBuyerSponsor) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return ino;
    }

    public List<String> getItemSaleSellerSponsor(double price) {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : itemSaleSellerSponsor) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return ino;
    }

    public List<String> getItemSaleSeller(double price) {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : itemSaleSeller) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return ino;
    }

    public List<String> getLoreForNewspaper(double price, String ownerName) {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : loreForNewspaper) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price).replace("<ownerName>", ownerName)));

        }

        return ino;
    }

    public List<String> getLeftarrowLore() {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : leftarrowLore) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return ino;
    }

    public String getLeftarrowTitle() {
        return ChatColor.translateAlternateColorCodes('&', leftarrowTitle);
    }

    public String getNewspaperTitle() {
        return ChatColor.translateAlternateColorCodes('&', newspaperTitle);
    }

    public List<String> getRightarrowLore() {
        ArrayList<String> ino = new ArrayList<>();

        for (String s : rightarrowLore) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return ino;
    }

    public String getRightarrowTitle() {
        return ChatColor.translateAlternateColorCodes('&', rightarrowTitle);
    }

    public String getPriceMessage(double price) {
        return ChatColor.translateAlternateColorCodes('&', priceMessage
                .replace("<value>", price + ""));
    }

    public String getUnlockedSlotPanelTitle() {
        return ChatColor.translateAlternateColorCodes('&', unlockedSlotPanelTitle);
    }

    public List<String> getUnlockedSlotPanelLore() {

        ArrayList<String> ino = new ArrayList<>();

        for (String s : unlockedSlotPanelLore) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return ino;
    }

    public String getLockedSlotPanelTitle() {
        return ChatColor.translateAlternateColorCodes('&',
                lockedSlotPanelTitle);
    }

    public List<String> getLockedSlotPanelLore() {

        ArrayList<String> ino = new ArrayList<>();

        for (String s : lockedSlotPanelLore) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }
        return ino;
    }

    public String getPutItem(double price, String type, int amount) {
        return ChatColor.translateAlternateColorCodes('&',
                putItem
                        .replace("<price>", price + "")
                        .replace("<type>", type.toLowerCase().replace("_", " "))
                        .replace("<amount>", amount + ""));
    }

    public String getSponsorSet(double price, String type, int amount) {
        return ChatColor.translateAlternateColorCodes('&',
                sponsorSet
                        .replace("<price>", price + "")
                        .replace("<type>", type.toLowerCase().replace("_", " "))
                        .replace("<amount>", amount + ""));
    }

    public String getNoAdv() {
        return ChatColor.translateAlternateColorCodes('&', noAdv);
    }

    public String getRemoveItem(double price, String type, int amount) {
        return ChatColor.translateAlternateColorCodes('&',
                removeItem
                        .replace("<price>", price + "")
                        .replace("<type>", type.toLowerCase().replace("_", " "))
                        .replace("<amount>", amount + ""));
    }

    public String getBoughtMessage(double price, String type, int amount, String name) {
        return ChatColor.translateAlternateColorCodes('&',
                boughtMessage
                        .replace("<price>", price + "")
                        .replace("<type>", type.toLowerCase().replace("_", " "))
                        .replace("<amount>", amount + "").replace("<name>", name));
    }

    public String getSellerMessage(double price, String type, int amount, String name) {
        return ChatColor.translateAlternateColorCodes('&',
                sellerMessage
                        .replace("<price>", price + "")
                        .replace("<type>", type.toLowerCase().replace("_", " "))
                        .replace("<amount>", amount + "").replace("<name>", name));
    }

    public String getSponsorButtonTitle() {
        return ChatColor.translateAlternateColorCodes('&', sponsorButtonTitle);
    }

    public List<String> getNotSponsoring(long minutes) {

        List<String> ret = new ArrayList<>();

        for (String i : notSponsoring) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", minutes + "")));
        }

        return ret;
    }

    public List<String> getNotSponsoringChange(long minutes) {
        List<String> ret = new ArrayList<>();

        for (String i : notSponsoringChange) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", minutes + "")));
        }

        return ret;
    }

    public List<String> getSponsoring(long minutes) {
        List<String> ret = new ArrayList<>();

        for (String i : sponsoring) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", minutes + "")));
        }

        return ret;
    }

    public List<String> getSponsoringChange(long minutes) {
        List<String> ret = new ArrayList<>();

        for (String i : sponsoringChange) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", minutes + "")));
        }

        return ret;
    }

    public List<String> getWaitToSponsor(long minutes, long minuteToSponsor) {
        List<String> ret = new ArrayList<>();

        for (String i : waitToSponsor) {
            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", minutes + "").replace("<missToSponsor>", minuteToSponsor + "")));

        }

        return ret;
    }

}
