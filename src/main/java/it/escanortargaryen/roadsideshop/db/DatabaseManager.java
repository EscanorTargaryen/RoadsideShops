package it.escanortargaryen.roadsideshop.db;

import com.google.common.base.Preconditions;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
import it.escanortargaryen.roadsideshop.classes.Shop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Encoding;
import org.sqlite.SQLiteConfig.SynchronousMode;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Class that handles all calls to the SQLite database.
 * We recommend that you use all of these database calls in async.
 */
public class DatabaseManager {

    /**
     * Database connection.
     */
    private final Connection connection;

    /**
     * All shops cached.
     * By default, only when a player enters the server or when an offline player's shop is requested with the command /roadsideshop <player> its shop is cached.
     */
    private static final ArrayList<Shop> cachedShops = new ArrayList<>();

    /**
     * Creates a new DatabaseManager.
     *
     * @param dbFile The file for the db.
     * @throws Exception SQL exceptions.
     */
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

    /**
     * Set up the database.
     *
     * @throws SQLException SQL exceptions.
     */
    public void setUp() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            statement.addBatch("CREATE TABLE IF NOT EXISTS `Players` (`UUID` TEXT NOT NULL PRIMARY KEY , `Name` TEXT NOT NULL);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Messages` (`Text` TEXT NOT NULL, `Key` INTEGER PRIMARY KEY AUTOINCREMENT, `UUID` TEXT NOT NULL, FOREIGN KEY(`UUID`) REFERENCES `Shop`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Shop` (`UUID` TEXT PRIMARY KEY NOT NULL,`Sponsor` TEXT,`LastSponsor` INT DEFAULT 0 NOT NULL, FOREIGN KEY(`UUID`) REFERENCES `Players`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.addBatch("CREATE TABLE IF NOT EXISTS `Items` (`Item` TEXT NOT NULL,`Slot` INT NOT NULL,`Price` REAL NOT NULL,`Shop` INT NOT NULL, PRIMARY KEY(`Shop`,`Slot`), FOREIGN KEY(`Shop`) REFERENCES `Shop`(`UUID`) ON DELETE CASCADE ON UPDATE CASCADE);");
            statement.executeBatch();

        }
    }

    /**
     * Returns the shop of a player. If his shop does not exist, it is created.
     *
     * @param player      The owner of the shop.
     * @param saveInCache Whether to cache the shop.
     * @return the shop of a player.
     */
    @Nullable
    public Shop getShop(@NotNull UUID player, boolean saveInCache) {
        Objects.requireNonNull(player);

        for (Shop sh : cachedShops) {

            if (sh.getPlayerUUID().equals(player)) {

                return sh;

            }
        }
        if (!isRegistered(player)) return null;
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

                Shop shop = new Shop(player, Objects.requireNonNull(getPlayerName(player)), getOffMessage(player), sponsor, i, r.getLong("LastSponsor"));
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
            // e.printStackTrace();
        }
        return null;

    }

    public static ArrayList<Shop> getCachedShops() {
        return new ArrayList<>(cachedShops);
    }

    /**
     * If the player has a shop, that is, if the player is registered in the "Players" table of the database.
     *
     * @param player A player.
     * @return If the player has a shop.
     * @see DatabaseManager#isRegistered(UUID)
     */
    public boolean hasShop(@NotNull UUID player) {

        Objects.requireNonNull(player);

        return isRegistered(player);

    }

    /**
     * Returns all stores registered in the database. They are not cached after use.
     *
     * @return all stores registered in the database.
     */
    public ArrayList<Shop> getAllShops() {
        ArrayList<Shop> ret = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Shop`;")) {
            ResultSet r = ps.executeQuery();
            while (r.next()) {

                ret.add(getShop(UUID.fromString(r.getString("UUID")), false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Saves the changes to the database.
     *
     * @param shop A shop.
     */
    public void updateShop(@NotNull Shop shop) {

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
                psInsert.setString(2, null);
                psInsert.setLong(3, shop.getLastSponsor());
                psInsert.executeUpdate();

                //deleteAllMessages(shop.getPlayerUUID());
                for (String s : shop.getOffMessages()) {
                    addOffMessage(shop.getPlayerUUID(), s);

                }

                for (SellingItem s : shop.getItems()) {
                    addItem(shop.getPlayerUUID(), s);

                }

                psInsert = connection.prepareStatement("UPDATE Shop SET `Sponsor` = ? WHERE `UUID` = ?;");
                psInsert.setString(1, sponsor);
                psInsert.setString(2, shop.getPlayerUUID().toString());

                psInsert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns all the items for sale in the shop.
     *
     * @param player The player owner of the shop.
     * @return all the items for sale in the shop.
     */
    public ArrayList<SellingItem> getItems(@NotNull UUID player) {

        Objects.requireNonNull(player);

        ArrayList<SellingItem> ret = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Items` WHERE `Shop`=?;")) {
            ps.setString(1, player.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                ret.add(new SellingItem(ItemSerializer.read(r.getString("Item"))[0], r.getInt("Slot"), r.getDouble("Price"), player));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Removes all the items in the shop.
     *
     * @param player The shop owner.
     */
    public void deleteAllItems(@NotNull UUID player) {
        Objects.requireNonNull(player);
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM `Items` WHERE `Shop`=?;")) {
            ps.setString(1, player.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Removes all the sale message while the owner was offline.
     *
     * @param player The shop owner.
     */
    public void deleteAllMessages(@NotNull UUID player) {

        Objects.requireNonNull(player);
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM `Messages` WHERE `UUID`=?;")) {
            ps.setString(1, player.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns all the sale message while the owner was offline.
     *
     * @param player The shop owner.
     */
    public ArrayList<String> getOffMessage(@NotNull UUID player) {
        Objects.requireNonNull(player);

        ArrayList<String> ret = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Messages` WHERE `UUID`=?;")) {
            ps.setString(1, player.toString());
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                ret.add(r.getString("Text"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void createShop(@NotNull UUID player) {
        Objects.requireNonNull(player);

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Shop`(`UUID`, `Sponsor`) VALUES(?,?);");
            psInsert.setString(1, player.toString());
            psInsert.setString(2, null);
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            //  ex.printStackTrace();

        }

    }

    /**
     * Add an item for sale in the shop.
     *
     * @param player      The owner of the shop.
     * @param sellingItem The item to be sold.
     */
    public void addItem(@NotNull UUID player, @NotNull SellingItem sellingItem) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(sellingItem);

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Items`(`Shop`, `Item`,`Slot`,`Price`) VALUES(?,?,?,?);");
            psInsert.setString(1, player.toString());
            psInsert.setString(2, ItemSerializer.write(sellingItem.getItem()));
            psInsert.setInt(3, sellingItem.getSlot());
            psInsert.setDouble(4, sellingItem.getPrice());
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Add a new sale message while the owner is offline.
     *
     * @param player The owner of the shop.
     * @param text   The message to be recorded.
     */
    private void addOffMessage(@NotNull UUID player, @NotNull String text) {

        Objects.requireNonNull(player);
        Objects.requireNonNull(text);

        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT INTO `Messages`(`UUID`, `Text`) VALUES(?,?);");
            psInsert.setString(1, player.toString());
            psInsert.setString(2, text);
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Returns the player's name.
     *
     * @param player The owner of the shop.
     * @return the player's name.
     */
    @Nullable
    public String getPlayerName(@NotNull UUID player) {
        Objects.requireNonNull(player);

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Players` WHERE `UUID`=?;")) {
            ps.setString(1, player.toString());
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                return r.getString(2);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * If the player is registered, that is, if the player is registered in the "Players" table of the database.
     *
     * @param player A player.
     * @return If the player is registered.
     */
    @Nullable
    public boolean isRegistered(@NotNull UUID player) {
        Objects.requireNonNull(player);

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `Players` WHERE `UUID`=?;")) {
            ps.setString(1, player.toString());
            ResultSet r = ps.executeQuery();
            return r.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * Add a new player to the database.
     *
     * @param player a new player.
     */
    public void addPlayer(@NotNull Player player) {
        Objects.requireNonNull(player);
        try {
            PreparedStatement psInsert = connection.prepareStatement("INSERT OR IGNORE INTO `Players`(`UUID`, `Name`) VALUES(?,?);");
            psInsert.setString(1, player.getUniqueId().toString());
            psInsert.setString(2, player.getName());
            psInsert.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Close the database connection.
     *
     * @throws SQLException SQL exceptions.
     */
    public void close() throws SQLException {
        connection.close();
    }

}
