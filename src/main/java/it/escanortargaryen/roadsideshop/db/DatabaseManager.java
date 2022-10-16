package it.escanortargaryen.roadsideshop.db;

import com.google.common.base.Preconditions;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Encoding;
import org.sqlite.SQLiteConfig.SynchronousMode;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class DatabaseManager {

    private final Connection connection;

    private static ArrayList<Shop> cachedShops = new ArrayList<>();

    public DatabaseManager(@NotNull File dbFile) throws Exception {
        Preconditions.checkNotNull(dbFile, "Database file is null.");

        if (!dbFile.exists() && !dbFile.createNewFile()) {
            throw new IOException("Cannot create the database file.");
        }
        Class.forName("org.sqlite.JDBC");
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setEncoding(Encoding.UTF8);
        config.setSynchronous(SynchronousMode.FULL);
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile, config.toProperties());
        setUp();
    }

    public void setUp() throws SQLException {
        try (Statement statement = connection.createStatement()) {

            statement.addBatch("CREATE TABLE IF NOT EXISTS `Players` (`UUID` TEXT NOT NULL PRIMARY KEY , `Name` TEXT NOT NULL);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Messages` (`Text` TEXT NOT NULL, `Key` INTEGER PRIMARY KEY AUTOINCREMENT, `UUID` TEXT NOT NULL, FOREIGN KEY(`UUID`) REFERENCES `Players`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Shop` (`UUID` TEXT PRIMARY KEY NOT NULL,`Sponsor` TEXT,`LastSponsor` INT DEFAULT 0 NOT NULL, FOREIGN KEY(`UUID`) REFERENCES `Players`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Items` (`Item` TEXT NOT NULL,`Slot` INT NOT NULL,`Price` REAL NOT NULL,`Shop` INT NOT NULL, PRIMARY KEY(`Shop`,`Slot`), FOREIGN KEY(`Shop`) REFERENCES `Shop`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.executeBatch();
        }
    }

    public Shop getShop(UUID player, boolean saveInCache) {

        for (Shop sh : cachedShops) {

            if (sh.getPlayerUUID().equals(player)) {

                return sh;

            }
        }

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Shop` WHERE `UUID`=?;")) {
            ps.setString(1, player.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {

                ArrayList<SellingItem> i = getItems(player);

                SellingItem sponsor = null;
                String s = r.getString("Sponsor");
                if (s != null) {

                    int slot = Integer.parseInt(s);

                    for (SellingItem ii : i) {

                        if (slot == ii.getSlot()) {

                            sponsor = ii;
                        }

                    }

                }

                Shop shop = new Shop(player, getPlayerName(player), getOffMessage(player), sponsor, i, r.getLong("LastSponsor"));
                if (saveInCache)
                    cachedShops.add(shop);
                return shop;

            } else {
                createShop(player);
                Shop shop = new Shop(player, getPlayerName(player));
                if (saveInCache)
                    cachedShops.add(shop);
                return shop;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static ArrayList<Shop> getCachedShops() {
        return new ArrayList<>(cachedShops);
    }

    public boolean hasShop(UUID player) {

        Objects.requireNonNull(player);

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Players` WHERE `UUID`=?;")) {
            ps.setString(1, player.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {

                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public ArrayList<Shop> getAlloShops() {
        ArrayList<Shop> ret = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Shop`;")) {
            ResultSet r = ps.executeQuery();
            while (r.next()) {

                ret.add(getShop(UUID.fromString(r.getString("UUID")),false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void updateShop(Shop shop) {

        Objects.requireNonNull(shop);

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Shop` WHERE `UUID`=?;")) {
            ps.setString(1, shop.getPlayerUUID().toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                String sponsor = null;

                if (shop.getSponsor() != null) {

                    sponsor = shop.getSponsor().getSlot() + "";

                }

                PreparedStatement psInsert = connection.prepareStatement("DELETE FROM `Shop` WHERE `UUID`=?;");
                psInsert.setString(1, shop.getPlayerUUID().toString());

                psInsert.executeUpdate();

                psInsert = connection.prepareStatement("INSERT INTO `Shop`(`UUID`, `Sponsor`, `LastSponsor`) VALUES(?,?,?);");
                psInsert.setString(1, shop.getPlayerUUID().toString());
                psInsert.setString(2, sponsor);
                psInsert.setLong(3, shop.getLastSponsor());
                psInsert.executeUpdate();

                deleteAllMessages(shop.getPlayerUUID());
                for (String s : shop.getOffMessages()) {
                    addOffMessage(shop.getPlayerUUID(), s);

                }
                deleteAllItems(shop.getPlayerUUID());
                for (SellingItem s : shop.getItems()) {
                    addItem(shop.getPlayerUUID(), s);

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<SellingItem> getItems(UUID p) {
        ArrayList<SellingItem> ret = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Items` WHERE `Shop`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                ret.add(new SellingItem(ItemSerializer.read(r.getString("Item"))[0], r.getInt("Slot"), r.getDouble("Price"), p));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void deleteAllItems(UUID p) {

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM `Items` WHERE `Shop`=?;")) {
            ps.setString(1, p.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteAllMessages(UUID p) {

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM `Messages` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getOffMessage(UUID p) {
        ArrayList<String> ret = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Messages` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                ret.add(r.getString("Text"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void createShop(UUID p) {

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Shop`(`UUID`, `Sponsor`) VALUES(?,?);");
            psInsert.setString(1, p.toString());
            psInsert.setString(2, null);
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();

        }

    }

    public void addItem(UUID p, SellingItem sellingItem) {
        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Items`(`Shop`, `Item`,`Slot`,`Price`) VALUES(?,?,?,?);");
            psInsert.setString(1, p.toString());
            psInsert.setString(2, ItemSerializer.write(sellingItem.getItem()));
            psInsert.setInt(3, sellingItem.getSlot());
            psInsert.setDouble(4, sellingItem.getPrice());
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void addOffMessage(UUID p, String text) {

        Objects.requireNonNull(p);
        Objects.requireNonNull(text);

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Messages`(`UUID`, `Text`) VALUES(?,?);");
            psInsert.setString(1, p.toString());
            psInsert.setString(2, text);
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public String getPlayerName(UUID p) {

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Players` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                return r.getString(2);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addPlayer(Player p) {

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT OR IGNORE INTO `Players`(`UUID`, `Name`) VALUES(?,?);");
            psInsert.setString(1, p.getUniqueId().toString());
            psInsert.setString(2, p.getName());
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void close() throws SQLException {
        connection.close();
    }

}
