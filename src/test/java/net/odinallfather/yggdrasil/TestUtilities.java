package net.odinallfather.yggdrasil;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.odinallfather.yggdrasil.core.YggdrasilManager;
import net.odinallfather.yggdrasil.core.YggdrasilParser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class TestUtilities {

    private static final YggdrasilManager MANAGER = YggdrasilInventoryPlugin.getInstance().getManager();

    public static YggdrasilParser getParser(String inventoryId) {
        return MANAGER.getParser(inventoryId);
    }

    public static InventoryClickEvent mockClickEvent(int slot, String inventoryId, ServerMock server, ItemStack stack) {
        PlayerMock player = server.addPlayer("AllfatherOdin___");
        Inventory inventory = isInventoryList(inventoryId) ? MANAGER.getInventoryPage(inventoryId, player, 0) : MANAGER.getInventory(inventoryId, player);
        InventoryView view = player.openInventory(inventory);
        return new InventoryClickEvent(view, InventoryType.SlotType.CONTAINER, slot, ClickType.LEFT, InventoryAction.PICKUP_ONE);
    }

    public static InventoryOpenEvent mockInventoryOpenEvent(String inventoryId, ServerMock server) {
        PlayerMock player = server.addPlayer("AllfatherOdin___");
        Inventory inventory = isInventoryList(inventoryId) ? MANAGER.getInventoryPage(inventoryId, player, 0) : MANAGER.getInventory(inventoryId, player);
        InventoryView view = player.openInventory(inventory);
        return new InventoryOpenEvent(view);
    }

    public static InventoryCloseEvent mockInventoryCloseEvent(String inventoryId, ServerMock server) {
        PlayerMock player = server.addPlayer("AllfatherOdin___");
        Inventory inventory = isInventoryList(inventoryId) ? MANAGER.getInventoryPage(inventoryId, player, 0) : MANAGER.getInventory(inventoryId, player);
        InventoryView view = player.openInventory(inventory);
        return new InventoryCloseEvent(view);
    }

    public static boolean isInventoryList(String inventoryId) {
        return MANAGER.getParser(inventoryId).isList();
    }

}
