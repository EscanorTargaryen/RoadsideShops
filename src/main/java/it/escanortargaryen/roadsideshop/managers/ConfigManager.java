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

    private int unlockedSlots;

    private String sponsorButtonTitle;

    private List<String> sponsoring, sponsoringChange, notSponsoring, notSponsoringChange, waitToSponsor;

    private String whileOffline,leftarrowTitle, rightarrowTitle, newspaperTitle, shopTitle, itemSettingsTitle, sellButtonTitle, priceButtonTitle, sellButtonTitleNotSet, priceButtonTitleNotSet, wrongPrice, anvilTitle, itemModify, backButtonTitle, removeButtonTitle, fullInv, noShop, noMoney;

    private List<String> sponsoredLore,leftarrowLore, rightarrowLore, itemSaleSeller, itemSaleBuyer, itemSaleSellerSponsor, itemSaleBuyerSponsor, loreForNewspaper, sellButtonLore, priceButtonLore, sellButtonLoreNotSet, priceButtonLoreNotSet, backButtonLore, removeButtonLore;

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

        if (!config.isSet("shop-title")) {
            throw new ConfigurationException("shop-title " + notSet);
        }
        shopTitle = config.getString("shop-title");

        if (!config.isSet("item-settings.title")) {
            throw new ConfigurationException("item-settings.title " + notSet);
        }
        itemSettingsTitle = config.getString("item-settings.title");

        if (!config.isSet("unlocked-slot")) {
            throw new ConfigurationException("unlocked-slot " + notSet);
        }
        unlockedSlots = config.getInt("unlocked-slot");
        if (unlockedSlots < 0 || unlockedSlots > 14) {
            throw new ConfigurationException("unlocked-slot must be positive and less than 15");

        }

        if (!config.isSet("item-settings.sell-button-price-set.title")) {
            throw new ConfigurationException("item-settings.sell-button-price-set.title " + notSet);
        }
        sellButtonTitle = config.getString("item-settings.sell-button-price-set.title");

        if (!config.isSet("item-settings.sell-button-price-set.lore")) {
            throw new ConfigurationException("item-settings.sell-button-price-set.lore " + notSet);
        }
        sellButtonLore = config.getStringList("item-settings.sell-button-price-set.lore");

        if (!config.isSet("item-settings.price-button-set.title")) {
            throw new ConfigurationException("item-settings.price-button-set-set.title " + notSet);
        }
        priceButtonTitle = config.getString("item-settings.price-button-set.title");

        if (!config.isSet("item-settings.price-button-set.lore")) {
            throw new ConfigurationException("item-settings.price-button-set.lore " + notSet);
        }
        priceButtonLore = config.getStringList("item-settings.price-button-set.lore");

        if (!config.isSet("item-settings.sell-button-price-not-set.title")) {
            throw new ConfigurationException("item-settings.sell-button-price-not-set-set.title " + notSet);
        }
        sellButtonTitleNotSet = config.getString("item-settings.sell-button-price-not-set.title");

        if (!config.isSet("item-settings.sell-button-price-not-set.lore")) {
            throw new ConfigurationException("item-settings.sell-button-price-not-set.lore " + notSet);
        }
        sellButtonLoreNotSet = config.getStringList("item-settings.sell-button-price-not-set.lore");

        if (!config.isSet("item-settings.price-button-not-set.title")) {
            throw new ConfigurationException("item-settings.price-button-not-set-set.title " + notSet);
        }
        priceButtonTitleNotSet = config.getString("item-settings.price-button-not-set.title");

        if (!config.isSet("item-settings.price-button-not-set.lore")) {
            throw new ConfigurationException("item-settings.price-button-not-set.lore " + notSet);
        }
        priceButtonLoreNotSet = config.getStringList("item-settings.price-button-not-set.lore");

        if (!config.isSet("wrong-price")) {
            throw new ConfigurationException("wrong-price " + notSet);
        }
        wrongPrice = config.getString("wrong-price");

        if (!config.isSet("anvil-title")) {
            throw new ConfigurationException("anvil-title " + notSet);
        }
        anvilTitle = config.getString("anvil-title");

        if (!config.isSet("item-modify.title")) {
            throw new ConfigurationException("item-modify.title " + notSet);
        }
        itemModify = config.getString("item-modify.title");

        if (!config.isSet("item-modify.back-button.title")) {
            throw new ConfigurationException("item-modify.back-button.title " + notSet);
        }
        backButtonTitle = config.getString("item-modify.back-button.title");

        if (!config.isSet("item-modify.back-button.lore")) {
            throw new ConfigurationException("item-modify.back-button.lore " + notSet);
        }
        backButtonLore = config.getStringList("item-modify.back-button.lore");

        if (!config.isSet("item-modify.remove-button.title")) {
            throw new ConfigurationException("item-modify.remove-button.title " + notSet);
        }
        removeButtonTitle = config.getString("item-modify.remove-button.title");

        if (!config.isSet("item-modify.remove-button.lore")) {
            throw new ConfigurationException("item-modify.remove-button.lore " + notSet);
        }
        removeButtonLore = config.getStringList("item-modify.remove-button.lore");

        if (!config.isSet("item-modify.full-inv")) {
            throw new ConfigurationException("item-modify.full-inv " + notSet);
        }
        fullInv = config.getString("item-modify.full-inv");

        if (!config.isSet("no-shop")) {
            throw new ConfigurationException("no-shop " + notSet);
        }
        noShop = config.getString("no-shop");

        if (!config.isSet("no-money")) {
            throw new ConfigurationException("no-money " + notSet);
        }
        noMoney = config.getString("no-money");

        if (!config.isSet("sponsor-button.sponsored")) {
            throw new ConfigurationException("sponsor-button.sponsored " + notSet);
        }
        sponsoredLore = config.getStringList("sponsor-button.sponsored");

        if (!config.isSet("while-offline")) {
            throw new ConfigurationException("while-offline " + notSet);
        }
        whileOffline = config.getString("while-offline");

    }

    public String getWhileOffline() {
        return ChatColor.translateAlternateColorCodes('&', whileOffline);
    }

    public List<String> getSponsoredLore() {

        List<String> ret = new ArrayList<>();

        for (String i : sponsoredLore) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", ConfigManager.SPONSORTIME/60 +"")));
        }

        return ret;
    }

    public String getNoMoney() {
        return noMoney;
    }

    public String getNoShop() {
        return ChatColor.translateAlternateColorCodes('&', noShop);
    }

    public String getFullInv() {

        return ChatColor.translateAlternateColorCodes('&', fullInv);
    }

    public String getRemoveButtonTitle() {

        return ChatColor.translateAlternateColorCodes('&', removeButtonTitle);
    }

    public List<String> getRemoveButtonLore() {

        ArrayList<String> t = new ArrayList<>();

        for (String s : removeButtonLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public List<String> getBackButtonLore() {

        ArrayList<String> t = new ArrayList<>();

        for (String s : backButtonLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public String getBackButtonTitle() {

        return ChatColor.translateAlternateColorCodes('&', backButtonTitle);
    }

    public String getItemModify() {

        return ChatColor.translateAlternateColorCodes('&', itemModify);
    }

    public String getAnvilTitle() {
        return anvilTitle;
    }

    public String getWrongPrice() {
        return wrongPrice;
    }

    public String getSellButtonTitle() {

        return ChatColor.translateAlternateColorCodes('&', sellButtonTitle);

    }

    public List<String> getPriceButtonLore(double price) {

        ArrayList<String> t = new ArrayList<>();

        for (String s : priceButtonLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s.replace("<price-message>", RoadsideShops.CONFIGMANAGER.getPriceMessage(price))));

        }

        return t;
    }

    public List<String> getSellButtonLore() {

        ArrayList<String> t = new ArrayList<>();

        for (String s : sellButtonLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public String getPriceButtonTitleNotSet() {

        return ChatColor.translateAlternateColorCodes('&', priceButtonTitleNotSet);

    }

    public String getSellButtonTitleNotSet() {

        return ChatColor.translateAlternateColorCodes('&', sellButtonTitleNotSet);

    }

    public List<String> getPriceButtonLoreNotSet() {

        ArrayList<String> t = new ArrayList<>();

        for (String s : priceButtonLoreNotSet) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public String getPriceButtonTitle() {

        return ChatColor.translateAlternateColorCodes('&', priceButtonTitle);

    }

    public List<String> getSellButtonLoreNotSet() {

        ArrayList<String> t = new ArrayList<>();

        for (String s : sellButtonLoreNotSet) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    public List<String> getItemSaleBuyer(double price) {
        ArrayList<String> t = new ArrayList<>();

        for (String s : itemSaleBuyer) {
            t.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return t;
    }

    public String getItemSettingsTitle() {
        return ChatColor.translateAlternateColorCodes('&', itemSettingsTitle);

    }

    public List<String> getItemSaleBuyerSponsor(double price) {
        ArrayList<String> t = new ArrayList<>();

        for (String s : itemSaleBuyerSponsor) {
            t.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return t;
    }

    public List<String> getItemSaleSellerSponsor(double price) {
        ArrayList<String> t = new ArrayList<>();

        for (String s : itemSaleSellerSponsor) {
            t.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return t;
    }

    public List<String> getItemSaleSeller(double price) {
        ArrayList<String> t = new ArrayList<>();

        for (String s : itemSaleSeller) {
            t.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price)));

        }

        return t;
    }

    public List<String> getLoreForNewspaper(double price, String ownerName) {
        ArrayList<String> t = new ArrayList<>();

        for (String s : loreForNewspaper) {
            t.add(ChatColor.translateAlternateColorCodes('&', s).replace("<price>", price + "").replace("<price-message>", getPriceMessage(price).replace("<ownerName>", ownerName)));

        }

        return t;
    }

    public List<String> getLeftarrowLore() {
        ArrayList<String> t = new ArrayList<>();

        for (String s : leftarrowLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public String getLeftarrowTitle() {
        return ChatColor.translateAlternateColorCodes('&', leftarrowTitle);
    }

    public String getNewspaperTitle() {
        return ChatColor.translateAlternateColorCodes('&', newspaperTitle);
    }

    public List<String> getRightarrowLore() {
        ArrayList<String> t = new ArrayList<>();

        for (String s : rightarrowLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
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

        ArrayList<String> t = new ArrayList<>();

        for (String s : unlockedSlotPanelLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        return t;
    }

    public String getLockedSlotPanelTitle() {
        return ChatColor.translateAlternateColorCodes('&',
                lockedSlotPanelTitle);
    }

    public List<String> getLockedSlotPanelLore() {

        ArrayList<String> t = new ArrayList<>();

        for (String s : lockedSlotPanelLore) {
            t.add(ChatColor.translateAlternateColorCodes('&', s));

        }
        return t;
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

    public String getShopTitle(String owner) {
        return ChatColor.translateAlternateColorCodes('&', shopTitle.replace("<playerName>", owner));
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

    public List<String> getNotSponsoring() {

        List<String> ret = new ArrayList<>();

        for (String i : notSponsoring) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", ConfigManager.SPONSORTIME/60 + "")));
        }

        return ret;
    }

    public List<String> getNotSponsoringChange() {
        List<String> ret = new ArrayList<>();

        for (String i : notSponsoringChange) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", ConfigManager.SPONSORTIME/60 +"")));
        }

        return ret;
    }

    public List<String> getSponsoring() {
        List<String> ret = new ArrayList<>();

        for (String i : sponsoring) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", ConfigManager.SPONSORTIME/60 +"")));
        }

        return ret;
    }

    public List<String> getSponsoringChange() {
        List<String> ret = new ArrayList<>();

        for (String i : sponsoringChange) {

            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", ConfigManager.SPONSORTIME/60 +"")));
        }

        return ret;
    }

    public List<String> getWaitToSponsor( long minuteToSponsor) {
        List<String> ret = new ArrayList<>();

        for (String i : waitToSponsor) {
            ret.add(ChatColor.translateAlternateColorCodes('&', i.replace("<minutes>", ConfigManager.SPONSORTIME/60 +"").replace("<missToSponsor>", minuteToSponsor + "")));

        }

        return ret;
    }

}
