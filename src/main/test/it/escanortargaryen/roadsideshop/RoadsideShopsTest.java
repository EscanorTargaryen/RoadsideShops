package it.escanortargaryen.roadsideshop;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RoadsideShopsTest {
    private ServerMock server;
    private RoadsideShops plugin;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        server.getServicesManager().register(Economy.class, new VaultEconomyTest(), RoadsideShops.INSTANCE, ServicePriority.High);
        MockPlugin g = MockBukkit.createMockPlugin("Vault");
        server.getPluginManager().enablePlugin(g);

        plugin = MockBukkit.load(RoadsideShops.class);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void test() {


    }
}
