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
import java.util.function.Supplier;

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
                    dataMap.put(inventoryId, new ListInventoryData(parser.getListInventories(), parser));
                } else {
                    dataMap.put(inventoryId, new DefaultInventoryData(parser.getInventory(), parser));
                }

                Bukkit.getPluginManager().registerEvents(new InventoryListener(parser), this.plugin);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Inventory getDynamicInventory(String inventoryId, Player player) {
        InventoryData data = this.dataMap.get(inventoryId);
        if(data == null)
            return null;
        YggdrasilParser parser = data.parser();
        return parser.getInventory(player);
    }

    @Nullable
    public Inventory getInventory(String inventoryId) {
        InventoryData data = this.dataMap.get(inventoryId);
        if (data == null)
            return null;
        return data.parser().getInventory();
    }

    public YggdrasilParser getParser(String inventoryId) {
        InventoryData data = this.dataMap.get(inventoryId);
        if (data == null)
            return null;
        return data.parser();
    }

    private interface InventoryData {

        Inventory inventory();

        Inventory[] inventories();

        boolean list();

        YggdrasilParser parser();

    }

    private record DefaultInventoryData(Inventory inventory, YggdrasilParser parser) implements InventoryData {

        @Override
        public Inventory[] inventories() {
            throw new UnsupportedOperationException("This method is not supported in this class");
        }

        @Override
        public boolean list() {
            return false;
        }
    }

    private record ListInventoryData(Inventory[] inventories, YggdrasilParser parser) implements InventoryData {

        @Override
        public Inventory inventory() {
            throw new UnsupportedOperationException("This method is not supported in this class");
        }

        @Override
        public boolean list() {
            return true;
        }
    }
}
