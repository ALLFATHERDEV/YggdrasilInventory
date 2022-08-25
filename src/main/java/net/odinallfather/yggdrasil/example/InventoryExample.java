package net.odinallfather.yggdrasil.example;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.YggdrasilItem;
import net.odinallfather.yggdrasil.core.annotation.*;
import net.odinallfather.yggdrasil.core.annotation.event.InventoryClose;
import net.odinallfather.yggdrasil.core.annotation.event.InventoryOpen;
import net.odinallfather.yggdrasil.util.InventoryHelper;
import net.odinallfather.yggdrasil.util.YggdrasilItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

@YggdrasilInventory(size = 27, title = "Test", id = InventoryExample.ID)
public class InventoryExample {

    public static final String ID = "test_inventory";

    public InventoryExample() {

    }

    @ItemAction(method = "callLoadingBar")
    @Item(slot = 0)
    public final YggdrasilItem diamond = new YggdrasilItemBuilder(Material.DIAMOND_AXE).withName(Component.text("Yeah", TextColor.color(Color.RED.getRGB()))).build();

    @ItemAction(method = "testMethod")
    @Item(slotRangeMin = 1, slotRangeMax = 5)
    public final YggdrasilItem axe = new YggdrasilItemBuilder(Material.GOLDEN_AXE).withName(Component.text("Test Axe", TextColor.color(Color.YELLOW.getRGB()))).build();

    @SwitchItem(id = "switch", executableMethod = "testMethod")
    @Item(slot = 9)
    public final YggdrasilItem green = new YggdrasilItemBuilder(Material.GREEN_WOOL).withName(Component.text("On", TextColor.color(Color.GREEN.getRGB()))).build();

    @SwitchItem(id = "switch", executableMethod = "otherTest")
    @Item
    public final YggdrasilItem red = new YggdrasilItemBuilder(Material.RED_WOOL).withName(Component.text("Off", TextColor.color(Color.GREEN.getRGB()))).build();

    @SwitchItem(id = "switch2", executableMethod = "testMethod")
    @Item(slot = 10)
    public final YggdrasilItem green2 = new YggdrasilItemBuilder(Material.GREEN_WOOL).withName(Component.text("On2", TextColor.color(Color.GREEN.getRGB()))).build();

    @SwitchItem(id = "switch2", executableMethod = "otherTest")
    @Item
    public final YggdrasilItem red2 = new YggdrasilItemBuilder(Material.RED_WOOL).withName(Component.text("Off2", TextColor.color(Color.GREEN.getRGB()))).build();

    @LoadingBarItem(id = "test")
    @Item
    public final YggdrasilItem loadingBarItem = new YggdrasilItemBuilder(Material.WHITE_WOOL).withName(Component.text("Loading Bar Item", TextColor.color(Color.GREEN.getRGB()))).build();

    @LoadingBarItem(id = "test", filler = true)
    @Item
    public final YggdrasilItem loadingBarItem2 = new YggdrasilItemBuilder(Material.BLUE_WOOL).withName(Component.text("Blue", TextColor.color(Color.BLUE.getRGB()))).build();

    @TextInput(methodName = "onAnvilFinish")
    @Item(slot = 8)
    public final YggdrasilItem anvil = new YggdrasilItemBuilder(Material.ANVIL).withName(Component.text("Text Input")).build();

    @Item
    public final YggdrasilItem d = new YggdrasilItemBuilder(Material.ACACIA_SLAB).withName(Component.text("Finish")).build();



    public void testMethod(Inventory inventory, Player player, ItemStack item) {
        player.sendMessage("Hey");
    }

    public void callLoadingBar(Inventory inventory, Player player, ItemStack item) {
        InventoryHelper.callLoadingBar(YggdrasilInventoryPlugin.getInstance().getManager(), ID, "test", player);
    }

    public void otherTest(Inventory inventory, Player player, ItemStack item) {
        player.sendMessage("Other test");
    }

    @LoadingBar(startPos = 18, width = 9, loadingBarItemId = "test", fillType = LoadingBar.FillType.FULL_TO_FULL)
    public void testLoadingBar(Inventory inventory, Player player, ItemStack item) {
        YggdrasilInventoryPlugin.LOGGER.info("it worked");
    }

    @InventoryOpen
    public void onOpen(Inventory inventory, Player player) {
        player.sendMessage("On Open");
    }
    @InventoryClose
    public void onClose(Inventory inventory, Player player) {
        player.sendMessage("On Close");
    }

    public AnvilGUI.Response onAnvilFinish(Player player, String text, Inventory oldInventory) {
        return AnvilGUI.Response.openInventory(oldInventory);
    }

}
