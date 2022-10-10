package it.escanortargaryen.roadsideshop.managers;

import it.escanortargaryen.roadsideshop.RoadsideShops;
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
    private String sponsorItemSet;
    private String boughtMessage;
    private String sellerMessage;
    private long sponsorTimeMills;

    private String sponsorButtonTitle;

    private List<String> sponsoring, sponsoringChange, notSponsoring, notSponsoringChange, waitToSponsor;

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

        priceMessage = config.getString("price-message");
        if (priceMessage == null) {
            throw new ConfigurationException("price-message " + notSet);
        }

        unlockedSlotPanelTitle = config.getString("unlocked-slot-panel-title");
        if (priceMessage == null) {
            throw new ConfigurationException("unlocked-slot-panel-title " + notSet);
        }

        unlockedSlotPanelLore = config.getStringList("unlocked-slot-lore");
        if (priceMessage == null) {
            throw new ConfigurationException("unlocked-slot-lore " + notSet);
        }

        lockedSlotPanelTitle = config.getString("locked-slot-panel-title");
        if (priceMessage == null) {
            throw new ConfigurationException("locked-slot-panel-title " + notSet);
        }

        lockedSlotPanelLore = config.getStringList("locked-slot-lore");
        if (priceMessage == null) {
            throw new ConfigurationException("locked-slot-lore " + notSet);
        }

        putItem = config.getString("put-item");
        if (priceMessage == null) {
            throw new ConfigurationException("put-item " + notSet);
        }

        sponsorSet = config.getString("sponsor-set");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-set " + notSet);
        }

        noAdv = config.getString("no-adv");
        if (priceMessage == null) {
            throw new ConfigurationException("no-adv " + notSet);
        }

        removeItem = config.getString("remove-item");
        if (priceMessage == null) {
            throw new ConfigurationException("remove-item " + notSet);
        }

        sponsorItemSet = config.getString("sponsor-item-set");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-item-set " + notSet);
        }

        boughtMessage = config.getString("bought-message");
        if (priceMessage == null) {
            throw new ConfigurationException("bought-message " + notSet);
        }

        sellerMessage = config.getString("seller-message");
        if (priceMessage == null) {
            throw new ConfigurationException("seller-message " + notSet);
        }

        sponsorTimeMills = config.getLong("sponsor-time-mills");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-time-mills " + notSet);
        }

        sponsorButtonTitle = config.getString("sponsor-button.title");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-button.title " + notSet);
        }

        sponsoring = config.getStringList("sponsor-button.sponsoring");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-button.sponsoring " + notSet);
        }

        sponsoringChange = config.getStringList("sponsor-button.sponsoring-change");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-button.sponsoring-change " + notSet);
        }

        notSponsoring = config.getStringList("sponsor-button.not-sponsoring");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-button.not-sponsoring " + notSet);
        }

        notSponsoringChange = config.getStringList("sponsor-button.not-sponsoring-change");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-button.not-sponsoring-change " + notSet);
        }

        waitToSponsor = config.getStringList("sponsor-button.wait");
        if (priceMessage == null) {
            throw new ConfigurationException("sponsor-button.wait " + notSet);
        }

    }

    public String getPriceMessage() {
        return priceMessage;
    }

    public String getUnlockedSlotPanelTitle() {
        return unlockedSlotPanelTitle;
    }

    public List<String> getUnlockedSlotPanelLore() {
        return unlockedSlotPanelLore;
    }

    public String getLockedSlotPanelTitle() {
        return lockedSlotPanelTitle;
    }

    public List<String> getLockedSlotPanelLore() {
        return lockedSlotPanelLore;
    }

    public String getPutItem() {
        return putItem;
    }

    public String getSponsorSet() {
        return sponsorSet;
    }

    public String getNoAdv() {
        return noAdv;
    }

    public String getRemoveItem() {
        return removeItem;
    }

    public String getSponsorItemSet() {
        return sponsorItemSet;
    }

    public String getBoughtMessage() {
        return boughtMessage;
    }

    public String getSellerMessage() {
        return sellerMessage;
    }

    public long getSponsorTimeMills() {
        return sponsorTimeMills;
    }

    public String getSponsorButtonTitle() {
        return sponsorButtonTitle;
    }

    public List<String> getNotSponsoring(long minutes) {

        List<String> ret = new ArrayList<>();

        for (String i : notSponsoring) {

            ret.add(i.replace("%minutes%", minutes + ""));
        }

        return ret;
    }

    public List<String> getNotSponsoringChange(long minutes) {
        List<String> ret = new ArrayList<>();

        for (String i : notSponsoringChange) {

            ret.add(i.replace("%minutes%", minutes + ""));
        }

        return ret;
    }

    public List<String> getSponsoring(long minutes) {
        List<String> ret = new ArrayList<>();

        for (String i : sponsoring) {

            ret.add(i.replace("%minutes%", minutes + ""));
        }

        return ret;
    }

    public List<String> getSponsoringChange(long minutes) {
        List<String> ret = new ArrayList<>();

        for (String i : sponsoringChange) {

            ret.add(i.replace("%minutes%", minutes + ""));
        }

        return ret;
    }

    public List<String> getWaitToSponsor(long minutes, long minuteToSponsor) {
        List<String> ret = new ArrayList<>();

        for (String i : waitToSponsor) {

            ret.add(i.replace("%minutes%", minutes + "").replace("%minutesToSponsor%", minuteToSponsor + ""));
        }

        return ret;
    }

}
