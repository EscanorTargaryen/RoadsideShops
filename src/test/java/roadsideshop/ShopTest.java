package roadsideshop;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import it.escanortargaryen.roadsideshop.InternalUtil;
import it.escanortargaryen.roadsideshop.RoadsideShops;
import it.escanortargaryen.roadsideshop.classes.SellingItem;
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

import static org.junit.Assert.*;

public class ShopTest {

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

        RoadsideShops.registerCustomLockedSlot(new ItemStack(Material.GOLD_BLOCK), (p) -> true);
    }

    @After
    public void tearDown() {

        MockBukkit.unmock();
    }

    @Test
    public void shopTest() {

        PlayerMock es = server.addPlayer("EscanorTargaryen");
        Mockito.timeout(100);

        SellingItem sellingItem = new SellingItem(new ItemStack(Material.ARROW), 1, 20.0, es.getUniqueId());

        Shop s = RoadsideShops.getShop(es);
        s.openInventory(es, ViewMode.SELLER);
        assertTrue(InternalUtil.INVENTORYHOLDERS.contains(es.getOpenInventory().getTopInventory().getHolder()));

        assertEquals(0, s.getItems().size());
        assertNull(s.getSponsor());

        s.addItem(sellingItem, false);

        assertEquals(1, s.getItems().size());

        assertNull(s.getSponsor());

        assertTrue(s.canSponsor());

        assertEquals(0, s.getMissTimeInMins());

        s.addItem(sellingItem, true);

        assertEquals(1, s.getItems().size());
        assertNotNull(s.getSponsor());

        assertFalse(s.canSponsor());

        assertTrue(s.getMissTimeInMins() == 5 || s.getMissTimeInMins() == 4);

    }

}
