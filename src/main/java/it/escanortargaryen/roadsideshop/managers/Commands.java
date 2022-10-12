package it.escanortargaryen.roadsideshop.managers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.Newspaper;
import it.escanortargaryen.roadsideshop.classes.Shop;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class Commands {

    public Commands() {
        enableNewsPaperCommand();
        enableRoadSideCommand();
    }

    private void enableRoadSideCommand() {
        new CommandAPICommand("roadsideshop").executesPlayer((p, objects) -> {

            if (!RoadsideShops.hasShop(p)) {

                RoadsideShops.createShop(p, new Shop(p.getUniqueId(), p.getName()));

            }
            RoadsideShops.getShop(p).openInventory(p, "seller");

        }).register();

        new CommandAPICommand("roadsideshop").withArguments(new OfflinePlayerArgument("shopOwner")).executesPlayer((p, objects) -> {

            OfflinePlayer shopOwner = (OfflinePlayer) objects[0];

            if (!RoadsideShops.hasShop(p)) {

                p.sendMessage(RoadsideShops.CONFIGMANAGER.getNoShop());

            } else {

                if (p.getName().equals(shopOwner)) {

                    RoadsideShops.getShop(shopOwner).openInventory(p, "seller");

                } else

                    RoadsideShops.getShop(shopOwner).openInventory(p, "buyer");

            }

        }).register();
    }

    private void enableNewsPaperCommand() {

        new CommandAPICommand("newspaper").executesPlayer((player, objects) -> {

            new Newspaper(RoadsideShops.getCachedShops(), player);
        }).register();

    }

}
