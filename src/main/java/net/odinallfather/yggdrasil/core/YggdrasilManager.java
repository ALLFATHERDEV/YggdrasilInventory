package net.odinallfather.yggdrasil.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dorkbox.annotation.AnnotationDefaults;
import dorkbox.annotation.AnnotationDetector;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.annotation.YggdrasilInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class YggdrasilManager {

    private final Plugin plugin;
    private final String packageName;

    private final Map<String, InventoryData> dataMap = Maps.newHashMap();

    private final List<InventoryData> data = Lists.newArrayList();
    private final List<ListInventoryData> listInventoryData = Lists.newArrayList();

    public YggdrasilManager(@NotNull Plugin plugin, @NotNull String packageName) {
        this.plugin = plugin;
        this.packageName = packageName;
    }

    public void load() {
        //Checking packages for inventory classes
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        try {
            List<Class<?>> classModules = AnnotationDetector.scanClassPath(classLoader, packageName)
                    .forAnnotations(YggdrasilInventory.class)
                    .on(ElementType.TYPE)
                    .collect(AnnotationDefaults.getType);
            if (classModules.isEmpty()) {
                YggdrasilInventoryPlugin.LOGGER.info("No class with @YggdrasilInventory annotation found");
                return;
            }
            classModules.forEach(inventoryClass -> {
                YggdrasilParser parser = new YggdrasilParser(inventoryClass);

                String inventoryId = parser.getInventoryId();
                if (inventoryId == null)
                    throw new NullPointerException("Could not find inventory id");

                if (parser.isList()) {
                    dataMap.put(inventoryId, new ListInventoryData(parser::getListInventories, parser));
                } else {
                    dataMap.put(inventoryId, new DefaultInventoryData(parser::getInventory, parser));
                }

                InventoryListener listener = new InventoryListener(parser);
                Bukkit.getPluginManager().registerEvents(listener, this.plugin);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Inventory getInventoryPage(String inventoryId, Player player, int page) {
        InventoryData data = this.dataMap.get(inventoryId);
        if (data == null)
            return null;
        if (data.list()) {
            Inventory[] inventories = data.inventories(player);
            if (page < 0 || page >= inventories.length)
                return null;
            return inventories[page];
        }
        return null;
    }

    @Nullable
    public Inventory getInventory(String inventoryId, Player player) {
        InventoryData data = this.dataMap.get(inventoryId);
        if (data == null)
            return null;
        return data.inventory(player);
    }

    public YggdrasilParser getParser(String inventoryId) {
        InventoryData data = this.dataMap.get(inventoryId);
        if (data == null)
            return null;
        return data.parser();
    }

    private interface InventoryData {

        default Inventory inventory(Player player) {
            return null;
        }

        default Inventory[] inventories(Player player) {
            return null;
        }

        boolean list();

        YggdrasilParser parser();

    }

    private record DefaultInventoryData(Function<Player, Inventory> inventory,
                                        YggdrasilParser parser) implements InventoryData {

        @Override
        public Inventory inventory(Player player) {
            return this.inventory.apply(player);
        }

        @Override
        public Inventory[] inventories(Player player) {
            throw new UnsupportedOperationException("This method is not supported in this class");
        }

        @Override
        public boolean list() {
            return false;
        }
    }

    private record ListInventoryData(Function<Player, Inventory[]> inventories,
                                     YggdrasilParser parser) implements InventoryData {

        public Inventory[] inventories(Player player) {
            return inventories.apply(player);
        }

        @Override
        public Inventory inventory(Player player) {
            throw new UnsupportedOperationException("This method is not supported in this class");
        }

        @Override
        public boolean list() {
            return true;
        }
    }
}
