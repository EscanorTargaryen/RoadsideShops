package it.davide.stands;

import org.bukkit.entity.Player;

public class StandUtils {

	static private StandManager plugin;

	public StandUtils(StandManager pl) {
		plugin = pl;

	}

	public static int getSlotsOfPlayer(Player p) {
		if (plugin.getStand(p) == null)
			return -1;
		else
			return plugin.getStand(p).getNormalSlots();
	}

}
