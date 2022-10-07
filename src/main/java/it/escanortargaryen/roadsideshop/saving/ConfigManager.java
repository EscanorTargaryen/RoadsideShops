package it.escanortargaryen.roadsideshop.saving;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
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
    private int sponsorTimeMills;

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
        sponsorTimeMills = config.getInt("sponsor-time-mills");
    }

    public YamlConfiguration getConfig() {
        return config;
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

    public int getSponsorTimeMills() {
        return sponsorTimeMills;
    }
}
