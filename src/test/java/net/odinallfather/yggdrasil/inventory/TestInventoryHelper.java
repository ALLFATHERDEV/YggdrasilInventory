package net.odinallfather.yggdrasil.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.odinallfather.yggdrasil.TestUtilities;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.InventoryListener;
import net.odinallfather.yggdrasil.core.YggdrasilItem;
import net.odinallfather.yggdrasil.util.YggdrasilItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.*;

class TestInventoryHelper {

    static ServerMock server;
    static YggdrasilInventoryPlugin plugin;

    final YggdrasilItem nextPage = new YggdrasilItemBuilder(Material.ARROW).withJUnitTestName("Next Page").build();
    final YggdrasilItem backPage = new YggdrasilItemBuilder(Material.ACACIA_BOAT).withJUnitTestName("Back Page").build();

    @BeforeAll
    static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(YggdrasilInventoryPlugin.class);
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Next Page Inventory")
    void testNextPage() {
        InventoryClickEvent event = TestUtilities.mockClickEvent(44, "test_list_inventory", server, nextPage.get());
        InventoryListener listener = new InventoryListener(TestUtilities.getParser("test_list_inventory"));
        listener.onClick(event);
        Assertions.assertEquals(Event.Result.ALLOW, event.getResult());
    }

    @Test
    @DisplayName("Prev Page Inventory")
    void testPrevPage() {
        InventoryClickEvent event = TestUtilities.mockClickEvent(36, "test_list_inventory", server, backPage.get());
        InventoryListener listener = new InventoryListener(TestUtilities.getParser("test_list_inventory"));
        listener.onClick(event);
        Assertions.assertEquals(Event.Result.DENY, event.getResult());
    }


}
