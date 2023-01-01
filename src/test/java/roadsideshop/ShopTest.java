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
        SellingItem sellingItem1 = new SellingItem(new ItemStack(Material.ARROW), 2, 20.0, es.getUniqueId());

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
        assertEquals(s.getSponsor(), sellingItem);

        assertFalse(s.canSponsor());

        assertTrue(s.getMissTimeInMins() == 4 || s.getMissTimeInMins() == 5);

        assertNotNull(s.getItemAt(1));
        assertNull(s.getItemAt(0));
        assertNull(s.getItemAt(-1));
        assertNull(s.getItemAt(3));

        s.addItem(sellingItem1, false);
        assertNotNull(s.getItemAt(2));
        assertEquals(2, s.getItems().size());
        assertEquals(s.getItemAt(1), sellingItem);
        assertEquals(s.getItemAt(2), sellingItem1);

        s.removeItem(1);
        assertNull(s.getItemAt(1));
        assertEquals(s.getItems().size(), 1);
        assertEquals(s.getItemAt(2), sellingItem1);

        s.removeItem(sellingItem1, es);
        assertNull(s.getItemAt(0));
        assertEquals(s.getItems().size(), 0);

        assertNull(s.getSponsor());
        s.setSponsor(sellingItem);

        s.addItem(sellingItem, false);
        s.setSponsor(sellingItem);
        assertNotNull(s.getSponsor());
        s.removeItem(1);
        s.addItem(sellingItem, false);
        s.setSponsor(1);
        assertNotNull(s.getSponsor());

        s.emptyItems();
        assertEquals(s.getItems().size(), 0);

    }

}
