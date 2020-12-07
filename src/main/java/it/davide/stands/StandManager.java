package it.davide.stands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import com.fren_gor.cmcSkyBlock.shop.SignUtilities;

import eu.endercentral.crazy_advancements.events.AdvancementGrantEvent;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import saving.SavingUtil;

public class StandManager extends JavaPlugin implements Listener {

	private HashMap<String, Stand> stands = new HashMap<>();
	private File config = new File(getDataFolder() + "/config.yml");

	private SavingUtil<Stand> savesStand;

	static public YamlConfiguration configconfig = new YamlConfiguration();
	@Getter
	static private HashMap<String, Integer> advancementSlot = new HashMap<>();
	@Getter
	static private HashMap<String, Integer> advancementPerms = new HashMap<>();

	public static ItemStack unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
	public static ItemStack not;
	public static ItemStack log;

	@Getter
	private static StandManager instance;

	private static Economy econ = null;

	// private ArrayList<Player> richieste = new ArrayList<>();

	public static Economy getEconomy() {
		return econ;
	}

	@Override
	public void onLoad() {
		saveResource("config.yml", false);
		new StandUtils(this);

	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = (Economy) rsp.getProvider();
		return econ != null;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Stand d = getStand(p);

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

	public void saveStand(Stand k) {

		savesStand.save(k);

	}

	public void saveAllStand() {

		for (Stand j : stands.values())

			savesStand.save(j);

	}

	private void registerAllStand() {
		savesStand.loadAll().forEach(k -> registerStand(k, false));

	}

	private void registerStand(Stand k, boolean save) {

		stands.put(k.getP().toString(), k);

		if (save)
			savesStand.save(k);

	}

	public void registerStand(Stand k) {

		registerStand(k, true);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {

		Bukkit.getPluginManager().registerEvents(this, this);
		instance = this;

		ConfigurationSerialization.registerClass(Stand.class, "Stand");
		ConfigurationSerialization.registerClass(SellingItem.class, "SellingItem");

		configconfig = YamlConfiguration.loadConfiguration(config);

		unlockedslot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta h = unlockedslot.getItemMeta();
		h.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				StandManager.configconfig.getString("unlocked-slot-panel-title")));

		ArrayList<String> ene = (ArrayList<String>) StandManager.configconfig.getList("unlocked-slot-lore");

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

		savesStand = new SavingUtil<Stand>(this, s -> s.getP().toString(), ".stand");
		registerAllStand();

		Map<String, Object> d = configconfig.getConfigurationSection("advancementSlot").getValues(false);

		for (Entry<String, Object> c : d.entrySet()) {

			advancementSlot.put(c.getKey().replace('!', ':'), (Integer) c.getValue());

		}

		Map<String, Object> b = configconfig.getConfigurationSection("permissionslot").getValues(false);

		for (Entry<String, Object> c : b.entrySet()) {

			advancementPerms.put(c.getKey().replace('_', '.'), (Integer) c.getValue());

		}

		not = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta w = not.getItemMeta();
		w.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				StandManager.configconfig.getString("locked-slot-panel-title")));

		ene = (ArrayList<String>) StandManager.configconfig.getList("locked-slot-lore");

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
				+ "§a	  / ____|| |                   | | §6/ ____|                        §fby §cEscanorTargaryen §fof   \n"
				+ "§a	 | (___  | |_  __ _  _ __    __| |§6| |      ___   _ __  ___             §6Command§7Craft \n"
				+ "§a	  \\___ \\ | __|/ _` || '_ \\  / _` |§6| |     / _ \\ | '__|/ _ \\     \n"
				+ "§a	  ____) || |_| (_| || | | || (_| |§6| |____| (_) || |  |  __/      §2Enabled version: "
				+ this.getDescription().getVersion() + "\n"
				+ "§a	 |_____/  \\__|\\__,_||_| |_| \\__,_| §6\\_____|\\___/ |_|   \\___|    \n";

		Bukkit.getConsoleSender().sendMessage(s);

	}

	public void copyFileUsingStream(File source, File dest) throws IOException {
		try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}

	public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
		try {
			ymlConfig.save(ymlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		saveAllStand();
	}

	public boolean containsPlayer(Player p) {

		return stands.containsKey(p.getUniqueId().toString());
	}

	public boolean containsPlayer(String name) {

		for (Stand s : stands.values()) {

			if (s.getPlayerName().equals(name))

				return true;

		}
		return false;

	}

	public Stand getStand(String playerName) {

		for (Stand s : stands.values()) {

			if (s.getPlayerName().equals(playerName))
				return s;

		}
		return null;

	}

	public Stand getStand(InventoryHolder f) {

		for (Stand s : stands.values()) {

			if (s.getHolder().equals(f))
				return s;

		}
		return null;

	}

	public Stand getStand(Player p) {

		return stands.get(p.getUniqueId().toString());
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {

		if (e.getView().getTopInventory() != null)
			if (getStand(e.getView().getTopInventory().getHolder()) == null)

				return;

		if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
			e.setCancelled(true);
			return;
		}

		if (e.getClickedInventory() == null || e.getCurrentItem() == null
				|| e.getCurrentItem().getType() == Material.AIR)
			return;

		Stand stand = getStand(e.getView().getTopInventory().getHolder());

		if (e.getWhoClicked().getUniqueId().equals(stand.getP())) {

			if (!e.getClickedInventory().getHolder().equals(stand.getHolder())) {

				if (e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT
						|| e.getClick() == ClickType.SHIFT_RIGHT) {

					e.setCancelled(true);

				}

				return;
			}

			if (stand.getItemAt(e.getSlot()) == null) {
				e.setCancelled(true);

				if (!stand.canSell(e.getSlot()))
					return;

				if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

					return;

				}

				new BukkitRunnable() {

					@Override
					public void run() {
						ItemStack i = e.getCursor().clone();
						e.getView().setCursor(new ItemStack(Material.AIR));

						new ItemSettingsIH(stand, i, (Player) e.getWhoClicked(), e.getSlot());

					}
				}.runTaskLater(this, 2);

			} else {

				e.setCancelled(true);
				new RemoveOSponsorIH(stand, stand.getItemAt(e.getSlot()), (Player) e.getWhoClicked());

			}

		} else {
			e.setCancelled(true);

			SellingItem venduto = null;

			if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {

				for (SellingItem c : stand.getItems()) {

					if (c.getSlot() == e.getSlot()) {

						if (getEconomy().has((OfflinePlayer) e.getWhoClicked(), c.getPrice())) {

							PlayerBuyStandEvent ev = new PlayerBuyStandEvent(stand, c, (Player) e.getWhoClicked());

							Bukkit.getPluginManager().callEvent(ev);

							if (!ev.isCancelled()) {

								getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), c.getPrice());
								getEconomy().depositPlayer(Bukkit.getOfflinePlayer(stand.getP()), c.getPrice());
								venduto = c;
								e.setCancelled(true);
								e.getWhoClicked().getInventory().addItem(c.getI());

								if (c.equals(stand.getSponsor())) {
									stand.getInvBuyer().setItem(stand.getSponsor().getSlot(),
											stand.getSponsor().getWithpriceBuyer());
									stand.getInvSeller().setItem(stand.getSponsor().getSlot(),
											stand.getSponsor().getWithpriceSeller());

									stand.setSponsor(null);

								}

								e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&',
										StandManager.configconfig.getString("bought-message")
												.replace("<price>", SignUtilities.formatVault(c.getPrice()))
												.replace("<type>",
														c.getI().getType().toString().toLowerCase().replace("_", " "))
												.replace("<amount>", c.getI().getAmount() + "")
												.replace("<name>", stand.getPlayerName())));

								if (Bukkit.getPlayer(stand.getP()) != null) {

									Bukkit.getPlayer(stand.getP()).sendMessage(ChatColor.translateAlternateColorCodes(
											'&',
											StandManager.configconfig.getString("seller-message")
													.replace("<price>", SignUtilities.formatVault(c.getPrice()))
													.replace("<type>",
															c.getI().getType().toString().toLowerCase().replace("_",
																	" "))
													.replace("<amount>", c.getI().getAmount() + "")
													.replace("<name>", stand.getPlayerName())));

								} else {
									stand.getOffMessages().add(ChatColor.translateAlternateColorCodes('&',
											StandManager.configconfig.getString("seller-message")

													.replace("<price>", SignUtilities.formatVault(c.getPrice()))
													.replace("<type>",
															c.getI().getType().toString().toLowerCase().replace("_",
																	" "))
													.replace("<amount>", c.getI().getAmount() + "")
													.replace("<name>", stand.getPlayerName())));

								}
								new BukkitRunnable() {

									@Override
									public void run() {

										stand.getInvSeller().setItem(c.getSlot(), new ItemStack(unlockedslot));

										stand.getInvBuyer().setItem(c.getSlot(), new ItemStack(Material.AIR));

									}
								}.runTaskLater(this, 2);

							}

						} else {
							e.setCancelled(true);
							e.getWhoClicked().sendMessage(ChatColor.RED + "You haven't enought money");
						}

					}
				}
				if (venduto != null) {

					stand.getItems().remove(venduto);

				}

			} else {
				e.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onadvancement(AdvancementGrantEvent e) {

		Player p = e.getPlayer();
		Stand stand = getStand(p);
		if (stand != null && p != null)
			stand.calculateSlots(p);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("stand")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Devi essere un player!");
				return false;
			}
			Player p = (Player) sender;

			if (args.length == 0) {

				if (!containsPlayer(p)) {

					registerStand(new Stand(p.getUniqueId(), p.getName().toString()));

					getStand(p).openInventory(p, "seller");

				} else {

					getStand(p).openInventory(p, "seller");

				}

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

		/*
		 * if (command.getName().equalsIgnoreCase("standsetlocation")) {
		 * 
		 * if (!(sender instanceof Player)) { sender.sendMessage(ChatColor.RED +
		 * "Devi essere un player!"); return false; } Player p = (Player) sender; if
		 * (!richieste.contains(p)) { richieste.add(p);
		 * 
		 * p.sendMessage(ChatColor.GOLD +
		 * "Click a block to set the location of your stand"); }
		 * 
		 * }
		 */

		return false;

	}

	/*
	 * @EventHandler per location public void onInteract(PlayerInteractEvent e) {
	 * 
	 * if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { Player p = e.getPlayer();
	 * 
	 * if (richieste.contains(e.getPlayer())) {
	 * 
	 * Stand s = getStand(e.getPlayer());
	 * 
	 * if (s != null) { s.setLocation(e.getClickedBlock().getLocation());
	 * 
	 * p.sendMessage(ChatColor.GOLD + "You setted the block of your stand"); } else
	 * p.sendMessage("You haven't a stand");
	 * 
	 * richieste.remove(e.getPlayer());
	 * 
	 * } else { for (Stand s : stands) {
	 * 
	 * if (e.getClickedBlock().getLocation().equals(s.getLocation())) {
	 * 
	 * Bukkit.dispatchCommand(p, "stand " + s.getPlayerName());
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * @EventHandler public void onBreak(BlockBreakEvent e) {
	 * 
	 * for (Stand s : stands) {
	 * 
	 * if (e.getBlock().getLocation().equals(s.getLocation())) {
	 * 
	 * e.getPlayer().sendMessage(ChatColor.GOLD + "Location disabled");
	 * s.setLocation(null);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 */

}
