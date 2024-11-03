package it.escanortargaryen.roadsideshop.managers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.Newspaper;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.classes.ViewMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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

        new CommandAPICommand(ConfigManager.SHOPCOMMAND).withPermission("roadsideshops.shopcommand").executesPlayer((p, objects) -> {

            openPersonalShop(p);

        }).register();

        new CommandAPICommand(ConfigManager.SHOPCOMMAND).withPermission("roadsideshops.shopcommand").withArguments(new OfflinePlayerArgument("shopOwner")).executesPlayer((p, objects) -> {

            OfflinePlayer shopOwner = (OfflinePlayer) objects.get(0);
            if (shopOwner != null) {

                openPlayerShop(p, shopOwner);

            }

        }).register();

        new CommandAPICommand("roadsideshopsadmin").withPermission("roadsideshops.admin.editshops").withArguments(new OfflinePlayerArgument("shopOwner")).executesPlayer((p, objects) -> {

            OfflinePlayer shopOwner = (OfflinePlayer) objects.get(0);
            if (shopOwner != null) {

                openPlayerShopAsSeller(p, shopOwner);

            }

        }).register();
    }

    private void enableNewsPaperCommand() {

        new CommandAPICommand(ConfigManager.NEWSPAPERCOMMAND).withPermission("roadsideshops.newspapercommand").executesPlayer((player, objects) -> {

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

    public static void openPersonalShop(Player p) {
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
    }

    public static void openPlayerShop(Player p, OfflinePlayer shopOwner) {
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

    public static void openPlayerShopAsSeller(Player p, OfflinePlayer shopOwner) {
        CompletableFuture.runAsync(() -> {
            if (!RoadsideShops.hasShop(shopOwner.getUniqueId())) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        p.sendMessage(InternalUtil.CONFIGMANAGER.getNoShop());
                    }
                }.runTask(RoadsideShops.INSTANCE);

            } else {

                Shop s = RoadsideShops.getShop(shopOwner.getUniqueId());
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        s.openInventory(p, ViewMode.SELLER);
                    }
                }.runTask(RoadsideShops.INSTANCE);

            }

        });
    }

}
