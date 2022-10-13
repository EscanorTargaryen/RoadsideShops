package it.escanortargaryen.roadsideshop;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import it.escanortargaryen.roadsideshop.classes.LockedSlot;
import it.escanortargaryen.roadsideshop.classes.LockedSlotCheck;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.Commands;
import it.escanortargaryen.roadsideshop.managers.ShopsManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static it.escanortargaryen.roadsideshop.InternalUtil.CONFIGMANAGER;

public class RoadsideShops extends JavaPlugin implements Listener {

    private static final HashMap<UUID, Shop> cachedShops = new HashMap<>();

    public static RoadsideShops INSTANCE;

    private static Economy econ = null;

    public static Economy getEconomy() {
        return econ;
    }

    private final ArrayList<LockedSlot> lockedSlots = new ArrayList<>();

    @Override
    public void onLoad() {
        INSTANCE = this;
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

        new InternalUtil();
        new ShopsManager();
        new Commands();

        String s = "§7----§c§nRoadside§r §6§nShops§r§7----§r\n§fby §eEscanorTargaryen§r\n§2Enabled version: " + this.getDescription().getVersion() + "§r\n§7-----------------------§r";
        Bukkit.getConsoleSender().sendMessage(s);

    }

    public static void registerCustomLockedSlot(ItemStack itemStack, LockedSlotCheck lockedSlotCheck) {

        INSTANCE.lockedSlots.add(new LockedSlot(itemStack, lockedSlotCheck));

    }

    public ArrayList<LockedSlot> getLockedSlots() {
        return new ArrayList<>(lockedSlots);
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
        Objects.requireNonNull(player);
        cachedShops.remove(player.getUniqueId());

    }

    public static void removeShop(Shop shop) {
        removeShop(shop.getPlayerUUID());

    }

    public static Collection<Shop> getCachedShops() {
        return cachedShops.values();
    }

    public static void createShop(Player player) {

        cachedShops.put(player.getUniqueId(), new Shop(player));

    }
}
