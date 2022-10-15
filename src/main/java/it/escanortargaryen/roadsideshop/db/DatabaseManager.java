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
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class DatabaseManager {

    private final Connection connection;

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

    }

    public void setUp() throws SQLException {
        try (Statement statement = connection.createStatement()) {

            statement.addBatch("CREATE TABLE IF NOT EXISTS `Players` (`UUID` TEXT NOT NULL PRIMARY KEY , `Name` TEXT NOT NULL ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Messages` (`Text` TEXT NOT NULL, `Key` INT NOT NULL AUTOINCREMENT, `UUID` TEXT NOT NULL, PRIMARY KEY(`Key`,`UUID`), FOREIGN KEY(`UUID`) REFERENCES `Players`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Shop` (`UUID` TEXT PRIMARY KEY NOT NULL,`Sponsor` TEXT, FOREIGN KEY(`UUID`) REFERENCES `Players`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Items` (`Item` TEXT NOT NULL,`Slot` INT NOT NULL,`Price` REAL NOT NULL,`Shop` INT NOT NULL, PRIMARY KEY(`Shop`,`Slot`), FOREIGN KEY(`Shop`) REFERENCES `Shop`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.executeBatch();
        }
    }

    public Shop getShop(UUID player) {

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

                return new Shop(player, getPlayerName(player), getOffMessage(player), sponsor, i);

            } else {
                createShop(player);
                return new Shop(player, getPlayerName(player));
            }
        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;

    }

    public void updateShop(Shop p) {

        Objects.requireNonNull(p);

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Shop` WHERE `UUID`=?;")) {
            ps.setString(1, p.getPlayerUUID().toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                String sponsor = null;

                if (p.getSponsor() != null) {

                    sponsor = p.getSponsor().getSlot() + "";

                }

                r.updateString("Sponsor", sponsor);
                deleteAllMessages(p.getPlayerUUID());
                for (String s : p.getOffMessages()) {
                    addOffMessage(p.getPlayerUUID(), s);

                }
                deleteAllItems(p.getPlayerUUID());
                for (SellingItem s : p.getItems()) {
                    addItem(p.getPlayerUUID(), s);

                }

            }
        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }

    public ArrayList<SellingItem> getItems(UUID p) {
        ArrayList<SellingItem> ret = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Items` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                ret.add(new SellingItem(ItemStackSerializer.deserializeItemStack(r.getString("Item")), r.getInt("Slot"), r.getDouble("Price"), p));
            }

        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return ret;
    }

    public void deleteAllItems(UUID p) {

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Items` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                r.deleteRow();
            }

        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }

    public void deleteAllMessages(UUID p) {

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Messages` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                r.deleteRow();
            }

        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }

    public ArrayList<String> getOffMessage(UUID p) {
        ArrayList<String> ret = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Messages` WHERE `UUID`=?;")) {
            ps.setString(1, p.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                ret.add(r.getString("Text"));

            }

        } catch (SQLException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
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
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

    }

    public void addItem(UUID p, SellingItem sellingItem) {
        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Items`(`Shop`, `Item`,`Slot`,`Price`,) VALUES(?,?,?,?);");
            psInsert.setString(1, p.toString());
            psInsert.setString(2, ItemStackSerializer.serializeItemStack(sellingItem.getItem()));
            psInsert.setInt(3, sellingItem.getSlot());
            psInsert.setDouble(4, sellingItem.getPrice());
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

    }

    private void addOffMessage(UUID p, String text) {

        Objects.requireNonNull(p);
        Objects.requireNonNull(text);

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Shop`(`UUID`, `Text`) VALUES(?,?);");
            psInsert.setString(1, p.toString());
            psInsert.setString(2, text);
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
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
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return "";
    }

    public void addPlayer(Player p) {

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Players`(`UUID`, `Name`) VALUES(?,?);");
            psInsert.setString(1, p.getUniqueId().toString());
            psInsert.setString(2, p.getName());
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

    }

    public void close() throws SQLException {
        connection.close();
    }

}
