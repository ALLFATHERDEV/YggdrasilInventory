package net.odinallfather.yggdrasil.util;

import net.odinallfather.yggdrasil.core.YggdrasilManager;
import net.odinallfather.yggdrasil.core.YggdrasilParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class InventoryHelper {

    public static void callLoadingBar(YggdrasilManager manager, String inventoryId, String loadingBarId, @Nullable Player player) {
        YggdrasilParser parser = manager.getParser(inventoryId);
        if (parser == null)
            throw new NullPointerException("Could not find parser for loading bar: " + loadingBarId);
        parser.callLoadingBar(loadingBarId, player);
    }

    public static boolean nextPage(YggdrasilManager manager, String inventoryId, Player player) {
        YggdrasilParser parser = manager.getParser(inventoryId);
        if (!parser.isList())
            return false;

        Inventory next = parser.getNextPage();
        if(next != null) {
            player.openInventory(next);
            return true;
        }
        return false;
    }

    public static boolean prevPage(YggdrasilManager manager, String inventoryId, Player player) {
        YggdrasilParser parser = manager.getParser(inventoryId);
        if (!parser.isList())
            return false;

        Inventory prev = parser.getPrevPage();
        if(prev != null) {
            player.openInventory(prev);
            return true;
        }
        return false;
    }

}
