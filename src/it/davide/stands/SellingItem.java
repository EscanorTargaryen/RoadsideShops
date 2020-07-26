package it.davide.stands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.fren_gor.cmcSkyBlock.shop.SignUtilities;

import lombok.Getter;

public class SellingItem implements Cloneable, ConfigurationSerializable {
	@Getter
	private ItemStack i;
	@Getter
	private ItemStack withprice;
	@Getter
	private ItemStack forNewspaper;

	@Getter
	private ItemStack withpriceESpondor;

	@Getter

	private int slot;
	@Getter
	private double price;
	@Getter
	private UUID p;

	public static SellingItem deserialize(Map<String, Object> args) {
		Validate.notNull(args, "Invalid args");

		return new SellingItem((ItemStack) args.get("item"), (int) args.get("slot"), (double) args.get("price"),
				UUID.fromString((String) args.get("player")));
	}

	SellingItem(ItemStack i, int slot, double price, UUID pl) {

		this.i = i;
		this.slot = slot;
		this.price = price;
		this.p = pl;

		ItemMeta m = i.getItemMeta();
		ArrayList<String> p = new ArrayList<>();
		p.add("");
		p.add(ChatColor.translateAlternateColorCodes('&',StandManager.configconfig.getString("price-message").replace("<value>",
				SignUtilities.formatVault(price))));
		if (m.getLore() != null)
			p.addAll(m.getLore());
		m.setLore(p);
		ItemStack h = i.clone();
		h.setItemMeta(m);
		withprice = h;

		ItemMeta ms = withprice.getItemMeta();
		ArrayList<String> p1 = new ArrayList<>();
		if (ms.getLore() != null)
			p1.addAll(ms.getLore());

		p1.add(ChatColor.AQUA + "Sponsored Item");

		ms.setLore(p1);
		ItemStack s = withprice.clone();
		s.setItemMeta(ms);
		withpriceESpondor = s;

		String nome = Bukkit.getOfflinePlayer(this.p).getName();
		forNewspaper = withprice.clone();
		ms = forNewspaper.getItemMeta();
		ArrayList<String> ar = new ArrayList<>();
		ar.addAll(ms.getLore());
		ar.add(ChatColor.YELLOW + "Owner: " + ChatColor.GRAY + nome);
		ar.add("");
		ar.add(ChatColor.GOLD + "Click to checkout " + nome + "'s stand");
		ms.setLore(ar);
		forNewspaper.setItemMeta(ms);

	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> map = new HashMap<>();
		map.put("item", i);
		map.put("slot", slot);
		map.put("price", price);
		map.put("player", p.toString());

		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((i == null) ? 0 : i.hashCode());
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + slot;
		result = prime * result + ((withprice == null) ? 0 : withprice.hashCode());
		result = prime * result + ((withpriceESpondor == null) ? 0 : withpriceESpondor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SellingItem)) {
			return false;
		}
		SellingItem other = (SellingItem) obj;
		if (i == null) {
			if (other.i != null) {
				return false;
			}
		} else if (!i.equals(other.i)) {
			return false;
		}
		if (p == null) {
			if (other.p != null) {
				return false;
			}
		} else if (!p.equals(other.p)) {
			return false;
		}
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price)) {
			return false;
		}
		if (slot != other.slot) {
			return false;
		}
		if (withprice == null) {
			if (other.withprice != null) {
				return false;
			}
		} else if (!withprice.equals(other.withprice)) {
			return false;
		}
		if (withpriceESpondor == null) {
			if (other.withpriceESpondor != null) {
				return false;
			}
		} else if (!withpriceESpondor.equals(other.withpriceESpondor)) {
			return false;
		}
		return true;
	}

}
