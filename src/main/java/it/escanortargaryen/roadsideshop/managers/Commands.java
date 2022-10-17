package it.escanortargaryen.roadsideshop.managers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.Newspaper;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.classes.ViewMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Command recorder.
 */
public class Commands {

    public Commands() {
        enableNewsPaperCommand();
        enableRoadSideCommand();
    }

    private void enableRoadSideCommand() {

        new CommandAPICommand(ConfigManager.SHOPCOMMAND).executesPlayer((p, objects) -> {

            CompletableFuture.runAsync(() -> {
                if (!RoadsideShops.hasShop(p.getUniqueId())) {

                    RoadsideShops.getShop(p);

                }

                Shop s = RoadsideShops.getShop(p);
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        s.openInventory(p, ViewMode.SELLER);

                    }
                }.runTask(RoadsideShops.INSTANCE);

            });

        }).register();

        new CommandAPICommand(ConfigManager.SHOPCOMMAND).withArguments(new OfflinePlayerArgument("shopOwner")).executesPlayer((p, objects) -> {

            OfflinePlayer shopOwner = (OfflinePlayer) objects[0];
            if (shopOwner != null) {

                CompletableFuture.runAsync(() -> {
                    if (!RoadsideShops.hasShop(shopOwner.getUniqueId())) {
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                p.sendMessage(InternalUtil.CONFIGMANAGER.getNoShop());
                            }
                        }.runTask(RoadsideShops.INSTANCE);

                    } else {

                        if (p.getUniqueId().equals(shopOwner.getUniqueId())) {

                            Shop s = RoadsideShops.getShop(shopOwner.getUniqueId());
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    s.openInventory(p, ViewMode.SELLER);
                                }
                            }.runTask(RoadsideShops.INSTANCE);

                        } else {

                            Shop s = RoadsideShops.getShop(shopOwner.getUniqueId());
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    s.openInventory(p, ViewMode.BUYER);

                                }
                            }.runTask(RoadsideShops.INSTANCE);

                        }

                    }

                });

            }

        }).register();
    }

    private void enableNewsPaperCommand() {

        new CommandAPICommand(ConfigManager.NEWSPAPERCOMMAND).executesPlayer((player, objects) -> {

            CompletableFuture.runAsync(() -> {
                ArrayList<Shop> shops = RoadsideShops.getAllShops();
                new BukkitRunnable() {

                    @Override

                    public void run() {
                        new Newspaper(shops, player);
                    }
                }.runTask(RoadsideShops.INSTANCE);

            });

        }).register();

    }

}
