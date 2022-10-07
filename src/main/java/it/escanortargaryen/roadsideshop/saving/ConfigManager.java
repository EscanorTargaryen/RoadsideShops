package it.escanortargaryen.roadsideshop.saving;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private YamlConfiguration config;

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
        loadAndValidateConfig();
    }

    public void loadAndValidateConfig() {
        priceMessage = config.getString("price-message");
        unlockedSlotPanelTitle = config.getString("unlocked-slot-panel-title");
        unlockedSlotPanelLore = config.getStringList("unlocked-slot-lore");
        lockedSlotPanelTitle = config.getString("locked-slot-panel-title");
        lockedSlotPanelLore = config.getStringList("locked-slot-lore");
        putItem = config.getString("put-item");
        sponsorSet = config.getString("sponsor-set");
        noAdv = config.getString("no-adv");
        removeItem = config.getString("remove-item");
        sponsorItemSet = config.getString("sponsor-item-set");
        boughtMessage = config.getString("bought-message");
        sellerMessage = config.getString("seller-message");
        sponsorTimeMills = config.getLong("sponsor-time-mills");
        sponsorButtonTitle = config.getString("sponsor-button.title");
        sponsoring = config.getStringList("sponsor-button.sponsoring");
        sponsoringChange = config.getStringList("sponsor-button.sponsoring-change");
        notSponsoring = config.getStringList("sponsor-button.not-sponsoring");
        notSponsoringChange = config.getStringList("sponsor-button.not-sponsoring-change");
        waitToSponsor = config.getStringList("sponsor-button.wait");
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
