package it.escanortargaryen.roadsideshop;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.Commands;
import it.escanortargaryen.roadsideshop.managers.ShopsManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static it.escanortargaryen.roadsideshop.InternalUtil.CONFIGMANAGER;

public class RoadsideShops extends JavaPlugin implements Listener {

    private static final HashMap<UUID, Shop> cachedShops = new HashMap<>();

    public static RoadsideShops INSTANCE;

    private static Economy econ = null;

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(false));

        saveResource("config.yml", false);

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!RoadsideShops.hasShop(p)) {

            RoadsideShops.createShop(p);

        }

        Shop d = getShop(p);

        if (d != null) {

            if (d.getOffMessages().size() > 0) {
                p.sendMessage(CONFIGMANAGER.getWhileOffline());
                for (String s : d.getOffMessages()) {

                    p.sendMessage(s);
                }

                d.getOffMessages().clear();

            }

        }

    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getServer().getLogger().info("Disabled due to no Vault economy found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CommandAPI.onEnable(this);

        Bukkit.getPluginManager().registerEvents(this, this);
        INSTANCE = this;

        new ShopsManager();
        new Commands();
        new InternalUtil();
        String s = "§7----§c§nRoadside§r §6§nShops§r§7----§r\n§fby §eEscanorTargaryen§r\n§2Enabled version: " + this.getDescription().getVersion() + "§r\n§7-----------------------§r";
        Bukkit.getConsoleSender().sendMessage(s);

    }

    public static boolean hasShop(Player player) {

        return hasShop(player.getUniqueId());

    }

    public static boolean hasShop(UUID player) {

        return cachedShops.containsKey(player);

    }

    public static Shop getShop(Player p) {

        return getShop(p.getUniqueId());
    }

    public static Shop getShop(UUID p) {

        return cachedShops.get(p);
    }

    public static void removeShop(UUID player) {
        cachedShops.remove(player);

    }

    public static void removeShop(Player player) {
        cachedShops.remove(player.getUniqueId());

    }

    public static void removeShop(Shop shop) {
        removeShop(shop.getPlayerUUID());

    }

    public static void clearShop(UUID player) {

        if (hasShop(player)) {
            getShop(player).clear();

        }

    }

    public static void removeItemShop() {
        //TODO

    }

    public static void addItem(Player player, SellingItem sellingItem, boolean isSponsoring, boolean sendMessage) {

       addItem(player.getUniqueId(), sellingItem, isSponsoring, sendMessage);

    }
    public static void addItem(Player player, SellingItem sellingItem) {

        addItem(player.getUniqueId(), sellingItem);

    }

    public static void addItem(UUID player, SellingItem sellingItem, boolean isSponsoring, boolean sendMessage) {

        if (hasShop(player)) {

            Shop s = getShop(player);
            addItem(s, sellingItem, isSponsoring, sendMessage);

        }

    }

    public static void addItem(UUID player, SellingItem sellingItem) {

        addItem(player, sellingItem);

    }

    public static void addItem(Shop shop, SellingItem sellingItem) {
        addItem(shop, sellingItem, false, false);

    }

    public static void addItem(Shop shop, SellingItem sellingItem, boolean isSponsoring, boolean sendMessage) {
        shop.addItem(sellingItem, isSponsoring, sendMessage, null);

    }

    public static Collection<Shop> getCachedShops() {
        return cachedShops.values();
    }

    public static void createShop(Player player) {

        cachedShops.put(player.getUniqueId(), new Shop(player));

    }
}
