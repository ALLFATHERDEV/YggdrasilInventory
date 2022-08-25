package net.odinallfather.yggdrasil.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.TestUtilities;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.YggdrasilItem;
import net.odinallfather.yggdrasil.core.YggdrasilParser;
import net.odinallfather.yggdrasil.example.InventoryExample;
import net.odinallfather.yggdrasil.example.ListInventoryExample;
import net.odinallfather.yggdrasil.example.TestDynamicInventory;
import net.odinallfather.yggdrasil.util.YggdrasilItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.junit.jupiter.api.*;

class TestYggdrasilParser {

    static ServerMock server;
    static YggdrasilInventoryPlugin plugin;
    final YggdrasilItem item = YggdrasilItemBuilder.buildDynamic(Material.CYAN_WOOL, (player, itemStackBuilder) -> itemStackBuilder.withName(Component.text(player.getName())).build());


    @BeforeAll
    static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(YggdrasilInventoryPlugin.class);
    }

    @Test
    @DisplayName("Dynamic Inventory Click")
    void testDynamicInventoryClick() {
        PlayerMock player = server.addPlayer("AllfatherOdin___");
        InventoryClickEvent event = TestUtilities.mockClickEvent(0, TestDynamicInventory.ID, server, item.get(player));
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryClick(event));
    }

    @Test
    @DisplayName("Dynamic Inventory Open")
    void testDynamicInventoryOpen() {
        InventoryOpenEvent event = TestUtilities.mockInventoryOpenEvent(TestDynamicInventory.ID, server);
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryOpen(event));
    }

    @Test
    @DisplayName("Dynamic Inventory Close")
    void testDynamicInventoryClose() {
        InventoryCloseEvent event = TestUtilities.mockInventoryCloseEvent(TestDynamicInventory.ID, server);
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryClose(event));
    }

    @Test
    @DisplayName("List Inventory Click")
    void testListInventoryClick() {
        PlayerMock player = server.addPlayer("AllfatherOdin___");
        InventoryClickEvent event = TestUtilities.mockClickEvent(0, ListInventoryExample.ID, server, item.get(player));
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryClick(event));
    }

    @Test
    @DisplayName("List Inventory Open")
    void tesListInventoryOpen() {
        InventoryOpenEvent event = TestUtilities.mockInventoryOpenEvent(ListInventoryExample.ID, server);
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryOpen(event));
    }

    @Test
    @DisplayName("List Inventory Close")
    void testListInventoryClose() {
        InventoryCloseEvent event = TestUtilities.mockInventoryCloseEvent(ListInventoryExample.ID, server);
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryClose(event));
    }

    @Test
    @DisplayName("Default Inventory Click")
    void testDefaulttInventoryClick() {
        PlayerMock player = server.addPlayer("AllfatherOdin___");
        InventoryClickEvent event = TestUtilities.mockClickEvent(0, InventoryExample.ID, server, item.get(player));
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryClick(event));
    }

    @Test
    @DisplayName("Default Inventory Open")
    void tesDefaultInventoryOpen() {
        InventoryOpenEvent event = TestUtilities.mockInventoryOpenEvent(InventoryExample.ID, server);
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryOpen(event));
    }

    @Test
    @DisplayName("Default Inventory Close")
    void testDefaultInventoryClose() {
        InventoryCloseEvent event = TestUtilities.mockInventoryCloseEvent(InventoryExample.ID, server);
        YggdrasilParser parser = TestUtilities.getParser(TestDynamicInventory.ID);
        Assertions.assertDoesNotThrow(() -> parser.handleInventoryClose(event));
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

}
