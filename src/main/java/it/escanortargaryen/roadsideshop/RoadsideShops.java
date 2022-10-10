package it.escanortargaryen.roadsideshop;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.saving.ConfigManager;
import it.escanortargaryen.roadsideshop.saving.SavingUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class RoadsideShops extends JavaPlugin implements Listener {

    private static final HashMap<String, Shop> cachedShops = new HashMap<>();

    private SavingUtil<Shop> savesStand;

    public static ConfigManager CONFIGMANAGER;

    public static ItemStack unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    public static ItemStack not;
    public static ItemStack log;

    private static RoadsideShops instance;

    private static Economy econ = null;

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true)); //Load with verbose output

        new CommandAPICommand("ping")
                .executes((sender, args) -> {
                    sender.sendMessage("pong!");
                })
                .register();
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
        Shop d = getStand(p);

        if (d != null) {

            if (d.getOffMessages().size() > 0) {
                p.sendMessage(ChatColor.AQUA + "While you was offline...");
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

    public void saveStand(Shop k) {

        savesStand.save(k);

    }

    public void saveAllStand() {

        for (Shop j : cachedShops.values())

            savesStand.save(j);

    }

    private void registerAllStand() {
        savesStand.loadAll().forEach(k -> registerStand(k, false));

    }

    private void registerStand(Shop k, boolean save) {
        return;
    }


    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getServer().getLogger().info("Disabled due to no Vault economy found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CommandAPI.onEnable(this);
        new Commands();
        Bukkit.getPluginManager().registerEvents(this, this);
        instance = this;

        ConfigurationSerialization.registerClass(Shop.class, "Stand");
        ConfigurationSerialization.registerClass(SellingItem.class, "SellingItem");

        CONFIGMANAGER = new ConfigManager(this);

        unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta h = unlockedslot.getItemMeta();
        Objects.requireNonNull(h).setDisplayName(ChatColor.translateAlternateColorCodes('&', CONFIGMANAGER.getUnlockedSlotPanelTitle()
        ));

        ArrayList<String> ene = (ArrayList<String>) CONFIGMANAGER.getUnlockedSlotPanelLore();

        ArrayList<String> ino = new ArrayList<>();

        for (String s : ene) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        h.setLore(ino);
        unlockedslot.setItemMeta(h);

        savesStand = new SavingUtil<>(this, s -> s.getPlayerUUID().toString(), ".stand");
        registerAllStand();

        not = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta w = not.getItemMeta();
        Objects.requireNonNull(w).setDisplayName(ChatColor.translateAlternateColorCodes('&',
                CONFIGMANAGER.getLockedSlotPanelTitle()));

        ene = (ArrayList<String>) CONFIGMANAGER.getLockedSlotPanelLore();

        ino = new ArrayList<>();

        for (String s : ene) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        h.setLore(ino);

        w.setLore(ino);
        not.setItemMeta(w);

        log = new ItemStack(Material.OAK_LOG);
        ItemMeta ws = log.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(ChatColor.WHITE + "");
        log.setItemMeta(ws);

        String s = "\n§a           _____  _                     _§6   _____                         \n"
                + "§a	  / ____|| |                   | | §6/ ____|                        §fby §cEscanorTargaryen   \n"
                + "§a	 | (___  | |_  __ _  _ __    __| |§6| |      ___   _ __  ___             \n"
                + "§a	  \\___ \\ | __|/ _` || '_ \\  / _` |§6| |     / _ \\ | '__|/ _ \\     \n"
                + "§a	  ____) || |_| (_| || | | || (_| |§6| |____| (_) || |  |  __/      §2Enabled version: "
                + this.getDescription().getVersion() + "\n"
                + "§a	 |_____/  \\__|\\__,_||_| |_| \\__,_| §6\\_____|\\___/ |_|   \\___|    \n";

        Bukkit.getConsoleSender().sendMessage(s);

    }

    @Override
    public void onDisable() {
        saveAllStand();
    }

    public static boolean hasShop(Player player) {

        for (Shop s : cachedShops.values()) {

            if (s.getPlayerUUID().equals(player.getUniqueId()))

                return true;

        }
        return false;

    }




    public static Shop getStand(Player p) {

        return getStand((OfflinePlayer) p);
    }

    public static Shop getStand(OfflinePlayer p) {

        return cachedShops.get(p.getUniqueId().toString());
    }



    public static RoadsideShops getInstance() {
        return instance;
    }

    public static Collection<Shop> getCachedShops() {
        return cachedShops.values();
    }

    public static void createShop(Player player, Shop shop) {

        cachedShops.put(player.getUniqueId().toString(), shop);

    }
}
