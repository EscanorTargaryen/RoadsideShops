package it.escanortargaryen.roadsideshop;

import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import it.escanortargaryen.roadsideshop.events.PlayerBuyStandEvent;
import it.escanortargaryen.roadsideshop.saving.ConfigManager;
import it.escanortargaryen.roadsideshop.saving.SavingUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class StandManager extends JavaPlugin implements Listener {

    private final HashMap<String, Shop> stands = new HashMap<>();

    private SavingUtil<Shop> savesStand;

    public static ConfigManager CONFIGMANAGER;

    public static ItemStack unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    public static ItemStack not;
    public static ItemStack log;

    private static StandManager instance;

    private static Economy econ = null;

    // private ArrayList<Player> richieste = new ArrayList<>();

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onLoad() {
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

        for (Shop j : stands.values())

            savesStand.save(j);

    }

    private void registerAllStand() {
        savesStand.loadAll().forEach(k -> registerStand(k, false));

    }

    private void registerStand(Shop k, boolean save) {
        return;
    }

    public void registerStand(Shop k) {

        registerStand(k, true);

    }

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);
        instance = this;

        ConfigurationSerialization.registerClass(Shop.class, "Stand");
        ConfigurationSerialization.registerClass(SellingItem.class, "SellingItem");

        CONFIGMANAGER = new ConfigManager(this);

        unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta h = unlockedslot.getItemMeta();
        h.setDisplayName(ChatColor.translateAlternateColorCodes('&', CONFIGMANAGER.getUnlockedSlotPanelTitle()
        ));

        ArrayList<String> ene = (ArrayList<String>) CONFIGMANAGER.getUnlockedSlotPanelLore();

        ArrayList<String> ino = new ArrayList<>();

        for (String s : ene) {
            ino.add(ChatColor.translateAlternateColorCodes('&', s));

        }

        h.setLore(ino);
        unlockedslot.setItemMeta(h);

        if (!setupEconomy()) {
            getServer().getLogger().info("Disabled due to no Vault economy found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        savesStand = new SavingUtil<>(this, s -> s.getPlayerUUID().toString(), ".stand");
        registerAllStand();

        not = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta w = not.getItemMeta();
        w.setDisplayName(ChatColor.translateAlternateColorCodes('&',
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
        ws.setDisplayName(ChatColor.WHITE + "");
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

    public boolean containsPlayer(String name) {

        for (Shop s : stands.values()) {

            if (s.getPlayerName().equals(name))

                return true;

        }
        return false;

    }

    public Shop getStand(String playerName) {

        for (Shop s : stands.values()) {

            if (s.getPlayerName().equals(playerName))
                return s;

        }
        return null;

    }

    public Shop getStand(InventoryHolder f) {

        for (Shop s : stands.values()) {

            if (s.getHolder().equals(f))
                return s;

        }
        return null;

    }

    public Shop getStand(Player p) {

        return stands.get(p.getUniqueId().toString());
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        e.getView().getTopInventory();
        if (getStand(e.getView().getTopInventory().getHolder()) == null)

            return;

        if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() == null || e.getCurrentItem() == null
                || e.getCurrentItem().getType() == Material.AIR)
            return;

        Shop shop = getStand(e.getView().getTopInventory().getHolder());

        if (e.getWhoClicked().getUniqueId().equals(shop.getPlayerUUID())) {

            if (e.getClickedInventory().getHolder() != (shop.getHolder())) {

                if (e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT
                        || e.getClick() == ClickType.SHIFT_RIGHT) {

                    e.setCancelled(true);

                }

                return;
            }

            if (shop.getItemAt(e.getSlot()) == null) {
                e.setCancelled(true);

                if (!shop.canSell(e.getSlot()))
                    return;

                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

                    return;

                }

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        ItemStack i = e.getCursor().clone();
                        e.getView().setCursor(new ItemStack(Material.AIR));

                        new ItemSettingsIH(shop, i, (Player) e.getWhoClicked(), e.getSlot());

                    }
                }.runTaskLater(this, 2);

            } else {

                e.setCancelled(true);
                new RemoveOSponsorIH(shop, shop.getItemAt(e.getSlot()), (Player) e.getWhoClicked());

            }

        } else {
            e.setCancelled(true);

            SellingItem venduto = null;

            if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

                for (SellingItem c : shop.getItems()) {

                    if (c.getSlot() == e.getSlot()) {

                        if (getEconomy().has((OfflinePlayer) e.getWhoClicked(), c.getPrice())) {

                            PlayerBuyStandEvent ev = new PlayerBuyStandEvent(shop, c, (Player) e.getWhoClicked());

                            Bukkit.getPluginManager().callEvent(ev);

                            if (!ev.isCancelled()) {
                                Player p = (Player) e.getWhoClicked();
                                switch (SafeInventoryActions.addItem(p.getInventory(), c.getI())) {

                                    case MODIFIED: {

                                        getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), c.getPrice());
                                        getEconomy().depositPlayer(Bukkit.getOfflinePlayer(shop.getPlayerUUID()), c.getPrice());
                                        venduto = c;
                                        e.setCancelled(true);

                                        if (c.equals(shop.getSponsor())) {
                                            shop.getInvBuyer().setItem(shop.getSponsor().getSlot(),
                                                    shop.getSponsor().getWithpriceBuyer());
                                            shop.getInvSeller().setItem(shop.getSponsor().getSlot(),
                                                    shop.getSponsor().getWithpriceSeller());

                                            shop.setSponsor(null);

                                        }

                                        e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                CONFIGMANAGER.getBoughtMessage()
                                                        .replace("<price>", c.getPrice() + "")
                                                        .replace("<type>",
                                                                c.getI().getType().toString().toLowerCase().replace("_", " "))
                                                        .replace("<amount>", c.getI().getAmount() + "")
                                                        .replace("<name>", shop.getPlayerName())));

                                        if (Bukkit.getPlayer(shop.getPlayerUUID()) != null) {

                                            Bukkit.getPlayer(shop.getPlayerUUID()).sendMessage(ChatColor.translateAlternateColorCodes(
                                                    '&',
                                                    CONFIGMANAGER.getSellerMessage()
                                                            .replace("<price>", c.getPrice() + "")
                                                            .replace("<type>",
                                                                    c.getI().getType().toString().toLowerCase().replace("_",
                                                                            " "))
                                                            .replace("<amount>", c.getI().getAmount() + "")
                                                            .replace("<name>", shop.getPlayerName())));

                                        } else {
                                            shop.getOffMessages().add(ChatColor.translateAlternateColorCodes('&',
                                                    CONFIGMANAGER.getSellerMessage()

                                                            .replace("<price>", c.getPrice() + "")
                                                            .replace("<type>",
                                                                    c.getI().getType().toString().toLowerCase().replace("_",
                                                                            " "))
                                                            .replace("<amount>", c.getI().getAmount() + "")
                                                            .replace("<name>", shop.getPlayerName())));

                                        }
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {

                                                shop.getInvSeller().setItem(c.getSlot(), new ItemStack(unlockedslot));

                                                shop.getInvBuyer().setItem(c.getSlot(), new ItemStack(Material.AIR));

                                            }
                                        }.runTaskLater(this, 2);
                                        break;
                                    }

                                    case NOT_MODIFIED:
                                    case NOT_ENOUGH_SPACE: {

                                        p.sendMessage(ChatColor.RED + "Inventory full");

                                        break;
                                    }

                                }

                            }

                        } else {
                            e.setCancelled(true);
                            e.getWhoClicked().sendMessage(ChatColor.RED + "You haven't enought money");
                        }

                    }
                }
                if (venduto != null) {

                    shop.getItems().remove(venduto);

                }

            } else {
                e.setCancelled(true);

            }

        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {

        if (command.getName().equalsIgnoreCase("stand")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Devi essere un player!");
                return false;
            }
            Player p = (Player) sender;

            if (args.length == 0) {

                if (!stands.containsKey(p.getUniqueId().toString())) {

                    stands.put(p.getUniqueId().toString(), new Shop(p.getUniqueId(), p.getName()));

                }
                getStand(p).openInventory(p, "seller");

            } else if (args.length == 1) {

                if (!containsPlayer(args[0])) {

                    p.sendMessage(ChatColor.RED + "The player doesn' t have a stand");

                } else {

                    if (p.getName().equals(args[0])) {

                        getStand(args[0]).openInventory(p, "seller");

                    } else

                        getStand(args[0]).openInventory(p, "buyer");

                }

            } else
                p.sendMessage(ChatColor.RED + "Usage: /stand <playername>");

        }

        if (command.getName().equalsIgnoreCase("newspaper")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Devi essere un player!");
                return false;
            }
            Player p = (Player) sender;

            new Newspaper(stands.values(), p);
            return true;

        }

        return false;

    }

    public static StandManager getInstance() {
        return instance;
    }

}
