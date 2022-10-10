package it.escanortargaryen.roadsideshop;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.managers.Commands;
import it.escanortargaryen.roadsideshop.managers.ConfigManager;
import it.escanortargaryen.roadsideshop.managers.ShopsManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class RoadsideShops extends JavaPlugin implements Listener {

    private static final HashMap<String, Shop> cachedShops = new HashMap<>();

    public static ConfigManager CONFIGMANAGER;

    public static ItemStack unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    public static ItemStack not;
    public static ItemStack log;

    public static RoadsideShops INSTANCE;

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
        Shop d = getShop(p);

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
        INSTANCE = this;

        CONFIGMANAGER = new ConfigManager(this);

        unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta h = unlockedslot.getItemMeta();
        Objects.requireNonNull(h).setDisplayName(CONFIGMANAGER.getUnlockedSlotPanelTitle()
        );

        h.setLore(CONFIGMANAGER.getUnlockedSlotPanelLore());
        unlockedslot.setItemMeta(h);

        not = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta w = not.getItemMeta();
        Objects.requireNonNull(w).setDisplayName(
                CONFIGMANAGER.getLockedSlotPanelTitle());

        w.setLore(CONFIGMANAGER.getLockedSlotPanelLore());
        not.setItemMeta(w);

        log = new ItemStack(Material.OAK_LOG);
        ItemMeta ws = log.getItemMeta();
        Objects.requireNonNull(ws).setDisplayName(ChatColor.WHITE + "");
        log.setItemMeta(ws);

        String s = "§7----§cRoadside §6Shops§7----§r\n§fby §cEscanorTargaryen§r\n§2Enabled version: " + this.getDescription().getVersion() + "§r\n§7------------------------§r";

        Bukkit.getConsoleSender().sendMessage(s);
        new ShopsManager();
    }

    public static boolean hasShop(Player player) {

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

    public static void createShop(Player player, Shop shop) {

        cachedShops.put(player.getUniqueId().toString(), shop);

    }
}
