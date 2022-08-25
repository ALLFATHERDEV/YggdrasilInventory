package net.odinallfather.yggdrasil.core;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface YggdrasilItem {

    ItemStack get(Player player);

    default ItemStack get() {
        return this.get(null);
    }

}
