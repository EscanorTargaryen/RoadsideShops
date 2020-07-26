package it.davide.stands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import it.davide.advancementAddOn.AdvancementAddOnUtils;
import lombok.Getter;
import lombok.Setter;

public class Stand implements Cloneable, ConfigurationSerializable, InventoryHolder {
	@Getter

	private UUID p;
	@Getter
	private Inventory invSeller = null;

	@Getter
	private Inventory invBuyer = null;

	@Getter
	private String playerName;

	@Getter
	@Setter
	private SellingItem sponsor = null;

	public final int normalSlotsMax = 16;

//	private final int autoSlotsMax = 2;
	// private int autoSlots = 0;
	@Getter
	@Setter
	private int normalSlots = 3;
	@Getter
	private ArrayList<SellingItem> items = new ArrayList<>();

	@Getter
	private ArrayList<String> offMessages = new ArrayList<>();

	@Getter

	private InventoryHolder holder = this;
	static public long timesponsor;
	private long lastsponsor = 0L;

	static {

		new BukkitRunnable() {

			@Override
			public void run() {
				Stand.timesponsor = StandManager.configconfig.getLong("sponsor-time-mills");

			}
		}.runTaskLater(StandManager.getInstance(), 40);
	}

	public boolean Checktime(long time) {

		if ((time - lastsponsor) > timesponsor) {

			return true;

		}

		return false;

	}

	public long getMissTimeinMins(long time) {

		return (timesponsor - (time - lastsponsor)) / 60000;

	}

	public void setTimeSponsor(long time) {

		lastsponsor = time;

	}

	public void openInventory(Player p, String mode) {

		if (mode.equals("seller"))
			if (invSeller == null) {

				invSeller = getInventory();

				p.openInventory(invSeller);
				StandManager.getInstance().saveStand(this);

			} else {
				StandManager.getInstance().saveStand(this);

				p.openInventory(invSeller);

			}
		else {
			if (invBuyer == null) {

				invSeller = getInventory();
				p.openInventory(invBuyer);

			} else
				p.openInventory(invBuyer);

		}

	}

	public void updateInventory() {

		invSeller = getInventory();

	}

	public SellingItem getItemAt(int slot) {

		for (SellingItem s : items) {
			if (s != null)
				if (s.getSlot() == slot)
					return s;
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Inventory getInventory() {
		normalSlots = 5;

		invSeller = Bukkit.createInventory(this, 18, ChatColor.DARK_BLUE + playerName + "'s stand");

		ItemStack here = StandManager.unlockedslot;
		ArrayList<String> ene = new ArrayList<>();
		ArrayList<String> ino = new ArrayList<>();

		ene = (ArrayList<String>) StandManager.configconfig.getList("locked-slot-lore");

		for (String s : ene) {
			ino.add(ChatColor.translateAlternateColorCodes('&',s));

		}

		for (Entry<String, Integer> s : StandManager.getAdvancementSlot().entrySet()) {

			if (AdvancementAddOnUtils.isAchievementGranted(p, s.getKey())) {

				setNormalSlots(normalSlots + s.getValue());

			}

		}

		for (int i = 0; i < 18; i++) {

			invSeller.setItem(i, StandManager.not);
		}

		for (int i = 1; i < normalSlots + 1; i++) {

			if (i > 7) {

				invSeller.setItem(i + 2, here);

			} else {
				invSeller.setItem(i, here);

			}

		}

		invSeller.setItem(0, StandManager.log);
		invSeller.setItem(8, StandManager.log);
		invSeller.setItem(9, StandManager.log);
		invSeller.setItem(17, StandManager.log);

		if (items != null)
			for (SellingItem s : items) {

				if (sponsor != null && s.equals(sponsor)) {

					ItemStack item = s.getWithpriceESpondor().clone();
					ItemMeta material = item.getItemMeta();

					ArrayList<String> arra = new ArrayList<>();
					arra.addAll(material.getLore());
					arra.add("");
					arra.add(ChatColor.GOLD + "Click to edit item");
					material.setLore(arra);
					item.setItemMeta(material);

					invSeller.setItem(s.getSlot(), item);

				} else {

					ItemStack ite = s.getWithprice().clone();
					ItemMeta me = ite.getItemMeta();

					ArrayList<String> a = new ArrayList<>();
					a.addAll(me.getLore());
					a.add("");
					a.add(ChatColor.GOLD + "Click to edit item");
					me.setLore(a);
					ite.setItemMeta(me);

					invSeller.setItem(s.getSlot(), ite);

				}

			}

		invBuyer = Bukkit.createInventory(this, 18, ChatColor.DARK_BLUE + playerName + "'s stand");

		invBuyer.setItem(0, StandManager.log);
		invBuyer.setItem(8, StandManager.log);
		invBuyer.setItem(9, StandManager.log);
		invBuyer.setItem(17, StandManager.log);

		if (items != null)
			for (SellingItem s : items) {

				if (sponsor != null && s.equals(sponsor))

				{

					ItemStack i = s.getWithpriceESpondor().clone();
					ItemMeta m = i.getItemMeta();

					ArrayList<String> arr = new ArrayList<>();
					arr.addAll(m.getLore());
					arr.add("");
					arr.add(ChatColor.GOLD + "Click to buy item");
					m.setLore(arr);
					i.setItemMeta(m);
					invBuyer.setItem(s.getSlot(), i);

				} else {
					ItemStack i = s.getWithprice().clone();
					ItemMeta m = i.getItemMeta();

					ArrayList<String> arr = new ArrayList<>();
					arr.addAll(m.getLore());
					arr.add("");
					arr.add(ChatColor.GOLD + "Click to buy item");
					m.setLore(arr);
					i.setItemMeta(m);

					invBuyer.setItem(s.getSlot(), i);

				}

			}

		return invSeller;
	}

	public boolean canSell(int slot) {

		if (slot > 0 && slot < 8) {

			if (slot <= normalSlots)
				return true;

		} else if (slot > 9 && slot < 17) {

			int temp = slot - 2;

			if (temp <= normalSlots)
				return true;
		}

		return false;
	}

	public Stand(UUID p, String name, ArrayList<SellingItem> m, SellingItem sponsor) {

		this.p = p;
		playerName = name;
		items = m;
		this.sponsor = sponsor;
	}

	public Stand(UUID p, String name) {

		this.p = p;
		playerName = name;
	}

	@SuppressWarnings("unchecked")
	public static Stand deserialize(Map<String, Object> args) {
		Validate.notNull(args, "Invalid args");

		return new Stand(UUID.fromString((String) args.get("playerUUID")), (String) args.get("playername"),
				(ArrayList<SellingItem>) args.get("items"), (SellingItem) args.get("sponsor"));
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> map = new HashMap<>();
		map.put("playerUUID", p.toString());
		map.put("items", items);
		map.put("playername", playerName);
		map.put("sponsor", sponsor);
		return map;
	}

	@Override
	public Stand clone() {
		try {
			return (Stand) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		result = prime * result + ((playerName == null) ? 0 : playerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Stand)) {
			return false;
		}
		Stand other = (Stand) obj;
		if (p == null) {
			if (other.p != null) {
				return false;
			}
		} else if (!p.equals(other.p)) {
			return false;
		}
		if (playerName == null) {
			if (other.playerName != null) {
				return false;
			}
		} else if (!playerName.equals(other.playerName)) {
			return false;
		}
		return true;
	}

}
