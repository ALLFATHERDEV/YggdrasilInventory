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

    public Inventory build(String title) {
        if (YggdrasilInventoryPlugin.isJUnitTest())
            return Bukkit.createInventory(null, size, title != null ? title : this.title());
        else
            return Bukkit.createInventory(null, size, Component.text(title != null ? title : this.title()));
    }

    @Override
    public Inventory[] buildList(String name, int itemCount) {
        throw new UnsupportedOperationException("Method not supported in defaut inventory info");
    }
}
