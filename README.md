# About
YggdrasilInventory is an annotation based inventory system. You can create inventories with using annotations.

# How to use
## Setup YggdrasilManager 
Setup ```YggdrasilManager``` in your onEnable method
```java
private YggdrasilManager manager;

@Override
public void onEnable() {
    manager = new YggdrasilManager(YourPlugin, packageName)
    manager.load();
}    
```
The packageName parameter should be the package name where your inventories are located. Because the library scans all files in this package for classes with the ``@YggdrasilInventory`` annotation

## Setup your first inventory
First you have to create a class, and this class must be annotated with ```@YggdrasilInventory```
This annotation need some information:

| Field         | Desription |
| -----         | ---------- |
| size          | Set the inventory size
| title         | Set the inventory title
| id            | Set the id for your inventory, so you can access to it later
| list*         | If this is true then this inventory will be a list inventory with multiple pages
| canMoveItems* | If this is true then the player can move items in the inventory

``list`` and ``canMoveItems`` are optional

## Add items to your inventory
### Default inventory
Every item that you want to add must be a single field in your class. Then the field need the annotation ``@Item``.
The ``@Item`` annotation is your primary and one of the important annotation because it tells yggdrasil where to place the item
You can choose between different placement styles:

| Field         | Description |
| -----         | ----------- |
| slot          | Here you can set the slot for this item |
| multipleSlots | In this array you can set multiple slots where the item should be placed
| slotRangeMin  | If you want to place a row of your item you can use this and the following field for setting the min and max position of the row
| slotRangeMax  | The max row position

But the item has no function yet. So, lets create a method that should be executed when the item is clicked:
```
public void someMethod(Inventory inventory, Player player, ItemStack clickedItem) {
    //Do soomething
}
```
The method should have these 3 parameters in the exact same order. Now, lets add the `´@ItemAction` annotation to your item. And the method name should be the same as the method that we created above:
```
@ItemAction(method = "someMethod)
@Item(slot = 0)
public final ItemStack item = new ItemStack(Material.DIAMOND);
```
**The fields must be public and not static!**

### Open the inventory
You can open the inventory with the following code:
```java
YggdrasilManager manager = ...;
Inventory inventory = manager.getInventory("inventoryId");
if(inventory != null) {
    player.openInventory(inventory);
}
```

## List Inventory
If you have a list of items that you want to display in an inventory, and don't want to handle all the different pages then you can use the list field in the ``@YggdrasilInventory`` annotation
After you set the list field to true, you have to put a method in your class:

````java
@ListItems
public List<ItemStack> getItems() {
    return new ArrayList<>(items);    
}
````
Don't forget the ``@ListItems`` annotation, so yggdrasil knows the method
Now you can add a page next and prev page item, like above in the default inventory. The last row is always free!

If you want to go to the next page or the previous page you can use the ``InventoryHelper`` class
There are two methods: ``nextPage(YggdrasilManager manager, String inventoryId, Player player)`` and ``prevPage(YggdrasilManager manager, String inventoryId, Player player)``


