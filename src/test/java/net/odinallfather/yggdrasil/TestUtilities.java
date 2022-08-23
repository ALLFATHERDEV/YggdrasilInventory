package net.odinallfather.yggdrasil;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.odinallfather.yggdrasil.core.YggdrasilManager;
import net.odinallfather.yggdrasil.core.YggdrasilParser;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class TestUtilities {

    private static final YggdrasilManager MANAGER = YggdrasilInventoryPlugin.getInstance().getManager();

    public static Inventory getInventory(String inventoryId) {
        return MANAGER.getInventory(inventoryId);
    }

    public static YggdrasilParser getParser(String inventoryId) {
        return MANAGER.getParser(inventoryId);
    }

    public static InventoryClickEvent mockClickEvent(int slot, String inventoryId, ServerMock server, ItemStack stack) {
        PlayerMock player = server.addPlayer();
        Inventory inventory = getInventory(inventoryId);
        InventoryView view = player.openInventory(inventory);
        return new InventoryClickEvent(view, InventoryType.SlotType.CONTAINER, slot, ClickType.LEFT, InventoryAction.PICKUP_ONE);
    }

}
