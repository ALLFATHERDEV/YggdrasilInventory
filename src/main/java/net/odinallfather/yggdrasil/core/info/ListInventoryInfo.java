package net.odinallfather.yggdrasil.core.info;

import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public record ListInventoryInfo(int rows, String title, boolean canMoveItems) implements InventoryInfo {

    @Override
    public int size() {
        return rows * 9;
    }

    @Override
    public boolean list() {
        return true;
    }

    @Override
    public Inventory build(String name) {
        throw new UnsupportedOperationException("Method not supported in list inventory info");
    }

    @Override
    public Inventory[] buildList(String title, int itemCount) {
        int size = this.size() - 9;
        if (size == 0)
            throw new IllegalArgumentException("Size must be greater then 9");

        int count = 0;
        for (int i = itemCount; i > 0; i -= size) {
            count++;
        }
        Inventory[] inventories = new Inventory[count];
        for (int i = 0; i < count; i++)
            if (YggdrasilInventoryPlugin.isJUnitTest())
                inventories[i] = Bukkit.createInventory(null, this.size(), title != null ? title : this.title);
            else
                inventories[i] = Bukkit.createInventory(null, this.size(), Component.text(this.title != null ? title : this.title));
        return inventories;
    }
}
