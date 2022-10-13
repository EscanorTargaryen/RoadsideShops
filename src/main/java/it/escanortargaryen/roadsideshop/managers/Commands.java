package it.escanortargaryen.roadsideshop.managers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.Newspaper;
import it.escanortargaryen.roadsideshop.classes.ViewMode;
import org.bukkit.OfflinePlayer;

public class Commands {

    public Commands() {
        enableNewsPaperCommand();
        enableRoadSideCommand();
    }

    private void enableRoadSideCommand() {

        new CommandAPICommand(ConfigManager.SHOPCOMMAND).executesPlayer((p, objects) -> {

            if (!RoadsideShops.hasShop(p)) {

                RoadsideShops.createShop(p);

            }
            RoadsideShops.getShop(p).openInventory(p, ViewMode.SELLER);

        }).register();

        new CommandAPICommand(ConfigManager.SHOPCOMMAND).withArguments(new OfflinePlayerArgument("shopOwner")).executesPlayer((p, objects) -> {

            OfflinePlayer shopOwner = (OfflinePlayer) objects[0];
            if (shopOwner != null) {

                if (!RoadsideShops.hasShop(shopOwner.getUniqueId())) {

                    p.sendMessage(InternalUtil.CONFIGMANAGER.getNoShop());

                } else {

                    if (p.getUniqueId().equals(shopOwner.getUniqueId())) {

                        RoadsideShops.getShop(shopOwner.getUniqueId()).openInventory(p, ViewMode.SELLER);

                    } else {
                        RoadsideShops.getShop(shopOwner.getUniqueId()).openInventory(p, ViewMode.BUYER);

                    }

                }
            }

        }).register();
    }

    private void enableNewsPaperCommand() {

        new CommandAPICommand(ConfigManager.NEWSPAPERCOMMAND).executesPlayer((player, objects) -> {

            new Newspaper(RoadsideShops.getCachedShops(), player);
        }).register();

    }

}
