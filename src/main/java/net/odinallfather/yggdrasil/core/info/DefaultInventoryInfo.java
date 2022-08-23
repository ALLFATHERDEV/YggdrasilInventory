package net.odinallfather.yggdrasil.core.info;

import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public record DefaultInventoryInfo(int size, String title,
                                   boolean canMoveItems) implements InventoryInfo {

    @Override
    public boolean list() {
        return false;
    }

    public Inventory build() {
        if (YggdrasilInventoryPlugin.isJUnitTest())
            return Bukkit.createInventory(null, size, title);
        else
            return Bukkit.createInventory(null, size, Component.text(title));
    }

    @Override
    public Inventory[] buildList(int itemCount) {
        throw new UnsupportedOperationException("Method not supported in defaut inventory info");
    }
}
