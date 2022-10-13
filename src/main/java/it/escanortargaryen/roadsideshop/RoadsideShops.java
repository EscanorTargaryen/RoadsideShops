package it.escanortargaryen.roadsideshop;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.Commands;
import it.escanortargaryen.roadsideshop.managers.ShopsManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

import static it.escanortargaryen.roadsideshop.InternalUtil.CONFIGMANAGER;

public class RoadsideShops extends JavaPlugin implements Listener {

    private static final HashMap<String, Shop> cachedShops = new HashMap<>();

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

        new BukkitRunnable() {

            @Override
            public void run() {
                if (d != null)
                    d.calculateSlots(p);
            }
        }.runTaskLater(this, 2);

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

    public static boolean hasShop(OfflinePlayer player) {

        for (Shop s : cachedShops.values()) {

            if (s.getPlayerUUID().equals(player.getUniqueId()))

                return true;

        }
        return false;

    }

    public static Shop getShop(Player p) {

        return getShop((OfflinePlayer) p);
    }

    public static Shop getShop(OfflinePlayer p) {

        return cachedShops.get(p.getUniqueId().toString());
    }

    public static Collection<Shop> getCachedShops() {
        return cachedShops.values();
    }

    public static void createShop(Player player) {

        cachedShops.put(player.getUniqueId().toString(), new Shop(player));

    }
}
