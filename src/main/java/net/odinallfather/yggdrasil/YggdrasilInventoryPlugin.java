package net.odinallfather.yggdrasil;

import net.odinallfather.yggdrasil.core.YggdrasilManager;
import net.odinallfather.yggdrasil.example.InventoryExample;
import net.odinallfather.yggdrasil.example.ListInventoryExample;
import net.odinallfather.yggdrasil.example.TestDynamicInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class YggdrasilInventoryPlugin extends JavaPlugin {

    private static YggdrasilInventoryPlugin instance;
    public static final Logger LOGGER = LoggerFactory.getLogger("Yggdrasil Inventory");

    private YggdrasilManager manager;

    //Constructor for MockBukkit
    public YggdrasilInventoryPlugin() {
        super();
    }

    protected YggdrasilInventoryPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        manager = new YggdrasilManager(this, "net.odinallfather.yggdrasil");
        manager.load();

        getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            Inventory inventory = manager.getDynamicInventory(TestDynamicInventory.ID, player);
            if (inventory != null) {
                player.openInventory(inventory);
            }
        } else {
            Inventory inventory = manager.getInventory(ListInventoryExample.ID);
            if (inventory != null)
                player.openInventory(inventory);
        }
        return true;
    }

    public static YggdrasilInventoryPlugin getInstance() {
        return instance;
    }

    public YggdrasilManager getManager() {
        return manager;
    }

    public static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit")) {
                return true;
            }
        }
        return false;
    }
}
