package net.odinallfather.yggdrasil.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public record InventoryListener(YggdrasilParser parser) implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        parser.handleInventoryClick(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        parser.handleInventoryOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        parser.handleInventoryClose(event);
    }

}
