package it.davide.stands;

import org.bukkit.entity.Player;

public class StandUtils {

	static private StandManager plugin;

	public StandUtils(StandManager pl) {
		plugin = pl;

	}

	public static int getSlotsOfPlayer(Player p) {

		return plugin.getStand(p).getNormalSlots();
	}

}
