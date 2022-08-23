package net.odinallfather.yggdrasil.core.info;

import org.bukkit.inventory.Inventory;

public interface InventoryInfo {

    int size();

    String title();

    boolean list();

    boolean canMoveItems();

    Inventory build();

    Inventory[] buildList(int itemCount);

}
