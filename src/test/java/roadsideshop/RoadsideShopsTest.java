package roadsideshop;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import it.escanortargaryen.roadsideshop.InternalUtil;
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

import java.util.UUID;

import static org.junit.Assert.*;

public class RoadsideShopsTest {
    private ServerMock server;
    private RoadsideShops plugin;
    private VaultEconomyTest vaultEconomyTest;

    private PlayerMock es;
    private PlayerMock fren;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        vaultEconomyTest = new VaultEconomyTest();
        server.getServicesManager().register(Economy.class, vaultEconomyTest, RoadsideShops.INSTANCE, ServicePriority.High);
        MockPlugin g = MockBukkit.createMockPlugin("Vault");
        server.getPluginManager().enablePlugin(g);

        plugin = MockBukkit.load(RoadsideShops.class);

        RoadsideShops.registerCustomLockedSlot(new ItemStack(Material.GOLD_BLOCK), (p) -> true);
    }

    @After
    public void tearDown() {

        MockBukkit.unmock();

    }

    @Test
    public void vaultTest() {

        es = server.addPlayer("EscanorTargaryen");
        Mockito.timeout(100);
        fren = server.addPlayer("fren_gor");
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


        apiTest();
    }


    public void apiTest() {

        assertTrue(RoadsideShops.hasShop(es.getUniqueId()));

        assertFalse(RoadsideShops.hasShop(new UUID(34243, 34243)));

        Shop shop = RoadsideShops.getShop(es);

        shop.openInventory(es, ViewMode.SELLER);
        assertTrue(InternalUtil.INVENTORYHOLDERS.contains(es.getOpenInventory().getTopInventory().getHolder()));
        assertEquals(es.getOpenInventory().getTopInventory().getItem(9 * 2 - 2).getType(), Material.GOLD_BLOCK);

        assertEquals(RoadsideShops.getAllShops().size(), 2);

    }
}
