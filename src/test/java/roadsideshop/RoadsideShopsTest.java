package roadsideshop;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.Shop;
import it.escanortargaryen.roadsideshop.classes.ViewMode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import roadsideshop.mock.VaultEconomyTest;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class RoadsideShopsTest {
    public static ServerMock server;
    private VaultEconomyTest vaultEconomyTest;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        vaultEconomyTest = new VaultEconomyTest();
        server.getServicesManager().register(Economy.class, vaultEconomyTest, RoadsideShops.INSTANCE, ServicePriority.High);
        MockPlugin g = MockBukkit.createMockPlugin("Vault");
        server.getPluginManager().enablePlugin(g);

        MockBukkit.load(RoadsideShops.class);

    }

    @After
    public void tearDown() {

        MockBukkit.unmock();
    }

    @Test
    public void vaultTest() {

        PlayerMock es = server.addPlayer("EscanorTargaryen");
        Mockito.timeout(100);
        PlayerMock fren = server.addPlayer("fren_gor");
        Mockito.timeout(100);
        vaultEconomyTest.registerPlayer(es);
        vaultEconomyTest.registerPlayer(fren);

        assertEquals(0.0, vaultEconomyTest.getBalance(es), 0);
        assertEquals(0.0, vaultEconomyTest.getBalance(fren), 0);

        assertFalse(vaultEconomyTest.has(es, 10));

        vaultEconomyTest.depositPlayer(es, 10);
        assertTrue(vaultEconomyTest.has(es, 10));

        vaultEconomyTest.depositPlayer(es, 10);
        assertTrue(vaultEconomyTest.has(es, 10));

        vaultEconomyTest.withdrawPlayer(es, 20);
        assertFalse(vaultEconomyTest.has(es, 10));
        mainClassTest();

    }

    public void mainClassTest() {

        PlayerMock es = server.getPlayer(1);

        assertTrue(RoadsideShops.hasShop(es.getUniqueId()));

        assertFalse(RoadsideShops.hasShop(new UUID(34243, 34243)));

        Shop shop = RoadsideShops.getShop(es);

        assertNotNull(shop);
        shop.openInventory(es, ViewMode.SELLER);
        RoadsideShops.registerCustomLockedSlot(new ItemStack(Material.GOLD_BLOCK), (p) -> false);
        assertEquals(Material.BLACK_STAINED_GLASS_PANE, Objects.requireNonNull(es.getOpenInventory().getTopInventory().getItem(9 * 2 - 2)).getType());
        RoadsideShops.registerCustomLockedSlot(new ItemStack(Material.GOLD_BLOCK), (p) -> true);

        assertEquals(Material.BLACK_STAINED_GLASS_PANE, Objects.requireNonNull(es.getOpenInventory().getTopInventory().getItem(9 * 2 - 2)).getType());

        shop.updateInventory();
        assertEquals(Material.GOLD_BLOCK, Objects.requireNonNull(es.getOpenInventory().getTopInventory().getItem(9 * 2 - 2)).getType());

        for (int i = 0; i < 30; i++) {

            RoadsideShops.registerCustomLockedSlot(new ItemStack(Material.DIAMOND_AXE), (p) -> true);

        }
        shop.updateInventory();
        assertEquals(Material.GOLD_BLOCK, Objects.requireNonNull(es.getOpenInventory().getTopInventory().getItem(9 * 2 - 2)).getType());
        assertEquals(Material.OAK_LOG, Objects.requireNonNull(es.getOpenInventory().getTopInventory().getItem(0)).getType());
        assertEquals(Material.DIAMOND_AXE, Objects.requireNonNull(es.getOpenInventory().getTopInventory().getItem(1)).getType());

        assertEquals(RoadsideShops.getAllShops().size(), 2);

        Shop shop1 = RoadsideShops.getShop(new UUID(34243, 34243));
        assertNull(shop1);

    }

}
