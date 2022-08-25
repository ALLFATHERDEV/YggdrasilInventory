package net.odinallfather.yggdrasil.core;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.annotation.*;
import net.odinallfather.yggdrasil.core.annotation.event.InventoryClose;
import net.odinallfather.yggdrasil.core.annotation.event.InventoryOpen;
import net.odinallfather.yggdrasil.core.info.DefaultInventoryInfo;
import net.odinallfather.yggdrasil.core.info.InventoryInfo;
import net.odinallfather.yggdrasil.core.info.ListInventoryInfo;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class YggdrasilParser {

    private final Class<?> inventoryClass;
    private Object inventoryClassObject;
    private final InventoryInfo defaultInventoryInfo;
    private Inventory inventory;
    private Inventory currentInventory;
    private String inventoryId;

    @Nullable
    private String dynamicTitle;

    @Nullable
    private Inventory[] listInventories;
    @Nullable
    private List<YggdrasilItem> listItems;
    private int currentPage = 0;

    private final Map<Slot, InventoryItem> slotMap = Maps.newHashMap();
    private final MethodAccess classMethods;
    private final FieldAccess classFields;
    private final Map<String, List<YggdrasilItem>> switchItems = Maps.newHashMap();
    //We don't want multiple slots
    private static int noSlotCount = 5000;
    private final Map<String, LoadingBarData> loadingBarData = Maps.newHashMap();
    private final Map<String, List<LoadingBarItemData>> loadingBarItemData = Maps.newHashMap();
    private final Map<String, Integer> loadingBarTasks = Maps.newHashMap();

    @Nullable
    private String inventoryOpenEventMethod;
    @Nullable
    private String inventoryCloseEventMethod;

    public YggdrasilParser(Class<?> inventoryClass) {
        this.inventoryClass = inventoryClass;
        this.classMethods = MethodAccess.get(inventoryClass);
        this.classFields = FieldAccess.get(inventoryClass);
        this.defaultInventoryInfo = readInventoryInfo();
        this.inventoryClassObject = ConstructorAccess.get(inventoryClass).newInstance();
        if (this.isList())
            this.parseListItems();
        else
            this.parseItems();
        this.lookForInventoryEvents();
    }


    @Contract(" -> new")
    private @NotNull InventoryInfo readInventoryInfo() {
        YggdrasilInventory annotation = this.inventoryClass.getAnnotation(YggdrasilInventory.class);
        if (annotation.size() % 9 != 0)
            throw new IllegalArgumentException("Inventory size is not correct");
        this.inventoryId = annotation.id();

        if (annotation.list()) {
            return new ListInventoryInfo(annotation.size() / 9, annotation.title(), annotation.canMoveItems());
        }
        return new DefaultInventoryInfo(annotation.size(), annotation.title(), annotation.canMoveItems());
    }

    private void parseListItems() {
        if (classFields.getFieldCount() == 0) {
            YggdrasilInventoryPlugin.LOGGER.warn("{} class has no item fields, skipping inventory class", inventoryClass.getSimpleName());
            return;
        }

        boolean listItemsFound = false;
        for (Method m : inventoryClass.getMethods()) {
            if (m.isAnnotationPresent(ListItems.class)) {
                this.listItems = (List<YggdrasilItem>) classMethods.invoke(inventoryClassObject, m.getName());
                listItemsFound = true;
                break;
            }
        }
        if (!listItemsFound) {
            YggdrasilInventoryPlugin.LOGGER.warn("Could not find list items for class: {}", inventoryClass.getSimpleName());
            return;
        }

        for (int i = 0, n = classFields.getFieldCount(); i < n; i++) {
            Field field = classFields.getFields()[i];
            this.parseItem(field, i);
        }
    }

    private void parseItems() {
        if (classFields.getFieldCount() == 0) {
            YggdrasilInventoryPlugin.LOGGER.warn("{} class has no item fields, skipping inventory class", inventoryClass.getSimpleName());
            return;
        }

        this.loadLoadingBars();
        for (int i = 0, n = classFields.getFieldCount(); i < n; i++) {
            Field field = classFields.getFields()[i];
            this.parseItem(field, i);
        }
    }

    private void lookForDynamicTitle(Player player) {
        for(Method m : this.inventoryClass.getDeclaredMethods()) {
            if(m.isAnnotationPresent(DynamicTitle.class)) {
                if(m.getParameterCount() != 1)
                    throw new YggdrasilParserException("DynamicTitle method has less or more than 1 parameter");
                if(!m.getReturnType().equals(String.class))
                    throw new YggdrasilParserException("DynamicTitle function must have a return type of a string");
                this.dynamicTitle = (String) this.classMethods.invoke(inventoryClassObject, m.getName(), player);
                break;
            }
        }
    }


    private void parseItem(Field field, int index) {
        if (field.isAnnotationPresent(Item.class)) {
            Item item = field.getAnnotation(Item.class);
            Slot slot = new Slot(item.slot(), item.multipleSlots(), item.slotRangeMin(), item.slotRangeMax());
            String executableMethod = "NONE";
            if (field.isAnnotationPresent(ItemAction.class)) {
                ItemAction action = field.getAnnotation(ItemAction.class);
                executableMethod = action.method();
            }
            YggdrasilItem itemStack = (YggdrasilItem) classFields.get(inventoryClassObject, index);
            String switchId = "NONE";

            //Check for SwitchItem
            if (field.isAnnotationPresent(SwitchItem.class)) {
                SwitchItem si = field.getAnnotation(SwitchItem.class);
                String id = si.id();
                if (switchItems.containsKey(id)) {
                    List<YggdrasilItem> items = switchItems.get(id);
                    items.add(itemStack);
                    switchItems.replace(id, items);
                } else {
                    switchItems.put(id, Lists.newArrayList(itemStack));
                }
                switchId = id;
                if (si.executableMethod().equals("NO_METHOD"))
                    executableMethod = "switch";
                else
                    executableMethod = "switch_" + si.executableMethod();
            }

            //Check for LoadingBarItem
            if (field.isAnnotationPresent(LoadingBarItem.class)) {
                LoadingBarItem loadingBarItem = field.getAnnotation(LoadingBarItem.class);
                if (this.loadingBarItemData.containsKey(loadingBarItem.id())) {
                    List<LoadingBarItemData> l = this.loadingBarItemData.get(loadingBarItem.id());
                    l.add(new LoadingBarItemData(this.loadingBarData.get(loadingBarItem.id()),
                            (YggdrasilItem) classFields.get(inventoryClassObject, index), loadingBarItem.filler()));
                    this.loadingBarItemData.replace(loadingBarItem.id(), l);
                } else
                    this.loadingBarItemData.put(loadingBarItem.id(), Lists.newArrayList(new LoadingBarItemData(this.loadingBarData.get(loadingBarItem.id()),
                            (YggdrasilItem) classFields.get(inventoryClassObject, index), loadingBarItem.filler())));
            }

            //Check for TextInput
            OptionFlag flag = null;
            if (field.isAnnotationPresent(TextInput.class)) {
                TextInput textInput = field.getAnnotation(TextInput.class);
                String methodName = textInput.methodName();
                flag = OptionFlag.TEXT_INPUT;
                executableMethod = methodName;
            }

            this.slotMap.put(slot, new InventoryItem(itemStack, executableMethod, switchId, flag));
        }
    }

    private void lookForInventoryEvents() {
        for (Method m : this.inventoryClass.getMethods()) {
            if (m.isAnnotationPresent(InventoryOpen.class)) {
                if (this.inventoryOpenEventMethod == null) {
                    this.inventoryOpenEventMethod = m.getName();
                } else {
                    throw new IllegalStateException("Found two InventoryOpen methods");
                }
            }
            if (m.isAnnotationPresent(InventoryClose.class)) {
                if (this.inventoryCloseEventMethod == null) {
                    this.inventoryCloseEventMethod = m.getName();

                } else {
                    throw new IllegalStateException("Found two InventoryClose methods");
                }

            }
        }
    }

    public boolean isList() {
        return this.defaultInventoryInfo.list();
    }

    public Inventory getNextPage(Player player) {
        if (this.currentPage + 1 >= this.listInventories.length)
            return null;
        this.currentPage++;
        return this.getListInventories(player)[this.currentPage];
    }

    public Inventory getPrevPage(Player player) {
        if (this.currentPage - 1 < 0)
            return null;
        this.currentPage--;
        return this.getListInventories(player)[this.currentPage];
    }

    public Inventory[] getListInventories(Player player) {
        if (this.defaultInventoryInfo instanceof ListInventoryInfo listInfo && listItems != null) {
            if (this.listInventories == null) {
                this.listInventories = this.defaultInventoryInfo.buildList(this.dynamicTitle, this.listItems.size());

                int index = 0;
                for (Inventory listInventory : listInventories) {
                    if (listInventory == null)
                        throw new NullPointerException("List inventory is null");

                    for (Map.Entry<Slot, InventoryItem> entry : this.slotMap.entrySet()) {
                        Slot s = entry.getKey();
                        InventoryItem item = entry.getValue();
                        s.place(item.item.get(player), listInventory);
                    }

                    int rows = listInfo.rows() - 1;
                    int totalCount = rows * 9;
                    for (int s = 0; s < totalCount; s++) {
                        if (index >= listItems.size())
                            break;
                        YggdrasilItem yggdrasilItem = listItems.get(index);
                        listInventory.setItem(s, yggdrasilItem.get(player));
                        index++;
                    }
                }
            }
            return listInventories;
        }
        throw new UnsupportedOperationException("Wrong inventory info");
    }

    public Inventory getInventory(Player player) {
        this.lookForDynamicTitle(player);
        if (this.isList()) {
            this.currentInventory = this.getListInventories(player)[currentPage];
        } else {
            this.currentInventory = this.getFinishedInventory(player);
        }
        return this.currentInventory;
    }

    public Inventory getInventory() {
        return this.getInventory(null);
    }

    private Inventory getFinishedInventory(Player player) {
        if (this.inventory != null)
            return this.inventory;
        this.inventory = this.defaultInventoryInfo.build(this.dynamicTitle);

        if (!this.slotMap.isEmpty()) {
            for (Map.Entry<Slot, InventoryItem> entry : this.slotMap.entrySet()) {
                Slot s = entry.getKey();
                InventoryItem item = entry.getValue();
                s.place(item.item.get(player), inventory);
            }
        }

        return inventory;
    }

    public String getInventoryId() {
        return inventoryId;
    }


    private AnvilGUI.Response tryToCallAnvilMethod(String method, Player player, Inventory inventory, String text) {
        try {
            int index = classMethods.getIndex(method);
            return (AnvilGUI.Response) this.classMethods.invoke(inventoryClassObject, index, player, text, inventory);
        } catch (IllegalArgumentException e) {
            YggdrasilInventoryPlugin.LOGGER.error("Method {} does not exist in inventory class {}", method, this.inventoryClass.getSimpleName());
        }
        return null;
    }

    private Object tryToCallMethod(String method, Player player, Inventory inventory, ItemStack clickedItem, boolean slim) {
        try {
            int index = classMethods.getIndex(method);
            Object res;
            if (slim) {
                res = this.classMethods.invoke(inventoryClassObject, index, inventory, player);
            } else {
                res = this.classMethods.invoke(inventoryClassObject, index, inventory, player, clickedItem);
            }
            if (res != null)
                return res;
        } catch (IllegalArgumentException e) {
            YggdrasilInventoryPlugin.LOGGER.error("Method {} does not exist in inventory class {}", method, this.inventoryClass.getSimpleName());
        }
        return null;
    }

    private Object tryToCallMethod(String method, Player player, Inventory inventory, ItemStack clickedItem) {
        return this.tryToCallMethod(method, player, inventory, clickedItem, false);
    }

    private String getSwitchId(ItemStack item, Player player) {
        for (InventoryItem inventoryItem : this.slotMap.values()) {
            if (inventoryItem.item.get(player).equals(item))
                return inventoryItem.switchId;
        }
        return "NONE";
    }

    private void loadLoadingBars() {
        Method[] method = this.inventoryClass.getMethods();
        for (Method m : method) {
            if (m.isAnnotationPresent(LoadingBar.class)) {
                LoadingBar loadingBar = m.getAnnotation(LoadingBar.class);
                loadingBarData.put(loadingBar.loadingBarItemId(), new LoadingBarData(loadingBar.startPos(), loadingBar.width(), m.getName(), loadingBar.fillType(), loadingBar.tick()));
            }
        }
    }

    private String getExecutableMethod(ItemStack item, Player player) {
        for (InventoryItem inventoryItem : this.slotMap.values()) {
            if (inventoryItem.item.get(player).equals(item))
                return inventoryItem.executeMethod;
        }
        return "NONE";
    }

    private OptionFlag getOption(ItemStack item, Player player) {
        for (InventoryItem inventoryItem : this.slotMap.values())
            if (inventoryItem.item.get(player).equals(item))
                return inventoryItem.flag;
        return null;
    }

    public void callLoadingBar(String id, @Nullable Player player) {
        List<LoadingBarItemData> dataList = loadingBarItemData.get(id);
        YggdrasilItem loadingBarItem = null;
        YggdrasilItem filler = null;

        if (dataList.size() == 1) {
            LoadingBarItemData loadingBarItemData = dataList.get(0);
            loadingBarItem = loadingBarItemData.item;
        } else {
            for (LoadingBarItemData lbid : dataList) {
                if (!lbid.filler) {
                    loadingBarItem = lbid.item;
                } else {
                    filler = lbid.item;
                }
            }
        }
        if (loadingBarItem == null)
            throw new NullPointerException("Could not find loading bar item");

        LoadingBarData d = dataList.get(0).data;
        int width = d.startPos + d.width - 1;
        AtomicInteger pos = new AtomicInteger(d.startPos);
        LoadingBar.FillType fillType = d.type;

        if (fillType == LoadingBar.FillType.FULL_TO_FULL) {
            if (filler == null)
                throw new NullPointerException("Could not find filler item");

            for (int i = d.startPos; i <= width; i++) {
                inventory.setItem(i, filler.get(player));
            }
        }

        final YggdrasilItem finalLoadingBarItem = loadingBarItem;
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(YggdrasilInventoryPlugin.getInstance(), () -> {
            inventory.setItem(pos.get(), finalLoadingBarItem.get(player));
            pos.getAndIncrement();
            if (pos.get() > width) {
                tryToCallMethod(d.methodToExecute, player, inventory, finalLoadingBarItem.get(player));
                this.cancelTasks(id);
            }
        }, 0, d.tick);

        this.loadingBarTasks.put(id, taskId);

    }


    private void cancelTasks(String id) {
        Bukkit.getScheduler().cancelTask(this.loadingBarTasks.get(id));
    }

    //========================HANDLE EVENTS========================

    public void handleInventoryClick(InventoryClickEvent event) {
        //Inventory toCheck = this.isList() ? this.listInventories[currentPage] : inventory;
        if (event.getInventory().equals(this.currentInventory)) {
            if (event.getCurrentItem() != null) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                String method = this.getExecutableMethod(clickedItem, (Player) event.getWhoClicked());
                if (!method.equals("NONE")) {
                    OptionFlag flag = this.getOption(clickedItem, (Player) event.getWhoClicked());
                    if (flag != null) {
                        if (flag == OptionFlag.TEXT_INPUT) {
                            new AnvilGUI.Builder()
                                    .onComplete((player1, s) -> tryToCallAnvilMethod(method, player1, this.currentInventory, s))
                                    .text("Please input your text")
                                    .title("Enter your text")
                                    .plugin(YggdrasilInventoryPlugin.getInstance())
                                    .open((Player) event.getWhoClicked());
                            event.setResult(Event.Result.ALLOW);
                            return;
                        }
                    }

                    if (method.startsWith("switch")) {
                        String switchId = this.getSwitchId(clickedItem, (Player) event.getWhoClicked());
                        if (switchId.equals("NONE")) {
                            YggdrasilInventoryPlugin.LOGGER.error("Something went wrong during switching from items");
                            event.setResult(Event.Result.DENY);
                            return;
                        }
                        List<YggdrasilItem> items = this.switchItems.get(switchId);
                        if (items == null || items.isEmpty()) {
                            YggdrasilInventoryPlugin.LOGGER.error("Something went wrong during switching from items");
                            event.setResult(Event.Result.DENY);
                            return;
                        }
                        ItemStack stack = items.stream().filter(yggitem -> yggitem.get((Player) event.getWhoClicked()).equals(clickedItem)).findFirst().get().get((Player) event.getWhoClicked());

                        int index = items.indexOf(stack);
                        if (index == -1) {
                            YggdrasilInventoryPlugin.LOGGER.error("Something went wrong during switching from items");
                            event.setResult(Event.Result.DENY);
                            return;
                        }

                        String executableMethod = method.contains("_") ? method.split("_")[1] : null;

                        int nextIndex = index + 1;
                        if (index == items.size() - 1) {
                            nextIndex = 0;
                        }
                        YggdrasilItem nextItem = items.get(nextIndex);
                        inventory.setItem(event.getSlot(), nextItem.get((Player) event.getWhoClicked()));

                        if (executableMethod != null) {
                            Object res = this.tryToCallMethod(executableMethod, (Player) event.getWhoClicked(), this.currentInventory, clickedItem);
                            if (res instanceof Event.Result)
                                event.setResult((Event.Result) res);
                        }
                        return;
                    }
                    Object res = this.tryToCallMethod(method, (Player) event.getWhoClicked(), this.currentInventory, clickedItem);
                    if (res instanceof Event.Result) {
                        event.setResult((Event.Result) res);
                    }
                }
            }
        }
    }

    public void handleInventoryOpen(InventoryOpenEvent event) {
        //Inventory toCheck = this.isList() ? this.listInventories[currentPage] : inventory;
        if (event.getInventory().equals(this.currentInventory)) {
            if (this.inventoryOpenEventMethod != null) {
                this.tryToCallMethod(this.inventoryOpenEventMethod, (Player) event.getPlayer(), event.getInventory(), null, true);
            }
        }
    }

    public void handleInventoryClose(InventoryCloseEvent event) {
        //Inventory toCheck = this.isList() ? this.listInventories[currentPage] : inventory;
        if (event.getInventory().equals(this.currentInventory)) {
            if (this.inventoryOpenEventMethod != null) {
                this.tryToCallMethod(this.inventoryCloseEventMethod, (Player) event.getPlayer(), event.getInventory(), null, true);
            }
        }
    }

    //========================DATA STORAGE CLASSES========================

    private record LoadingBarData(int startPos, int width, String methodToExecute, LoadingBar.FillType type, int tick) {

    }

    private record LoadingBarItemData(LoadingBarData data, YggdrasilItem item, boolean filler) {

    }

    private record InventoryItem(YggdrasilItem item, String executeMethod, String switchId, OptionFlag flag) {

    }

    private enum OptionFlag {

        TEXT_INPUT

    }

    private record Slot(int slot, int[] multipleSlots, int minSlot, int maxSlot) {

        Slot {
            if (slot == -1 && multipleSlots.length == 0 && minSlot == -1 && maxSlot == -1)
                slot = noSlotCount++;
        }

        void place(ItemStack stack, Inventory inventory) {
            if (slot >= 5000)
                return;
            if (this.slot != -1) {
                inventory.setItem(slot, stack);
                return;
            }
            if (multipleSlots.length > 0) {
                for (int s : multipleSlots)
                    inventory.setItem(s, stack);
                return;
            }
            if (minSlot != -1 && maxSlot != -1) {
                for (int i = minSlot; i <= maxSlot; i++)
                    inventory.setItem(i, stack);
                return;
            }
            throw new YggdrasilParserException("Could not place items");
        }

    }

}
