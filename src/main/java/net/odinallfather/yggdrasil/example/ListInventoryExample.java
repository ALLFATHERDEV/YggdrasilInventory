package net.odinallfather.yggdrasil.example;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.YggdrasilItem;
import net.odinallfather.yggdrasil.core.annotation.Item;
import net.odinallfather.yggdrasil.core.annotation.ItemAction;
import net.odinallfather.yggdrasil.core.annotation.ListItems;
import net.odinallfather.yggdrasil.core.annotation.YggdrasilInventory;
import net.odinallfather.yggdrasil.util.InventoryHelper;
import net.odinallfather.yggdrasil.util.YggdrasilItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.List;

@YggdrasilInventory(size = 45, title = "TestList", list = true, id = ListInventoryExample.ID)
public class ListInventoryExample {

    public static final String ID = "test_list_inventory";

    @ItemAction(method = "nextPage")
    @Item(slot = 44)
    public final YggdrasilItem nextPage = new YggdrasilItemBuilder(Material.ARROW).withJUnitTestName("Next Page").build();

    @ItemAction(method = "backPage")
    @Item(slot = 36)
    public final YggdrasilItem backPage = new YggdrasilItemBuilder(Material.ACACIA_BOAT).withJUnitTestName("Back Page").build();

    public Event.Result nextPage(Inventory inventory, Player player, ItemStack stack) {
        boolean res = InventoryHelper.nextPage(YggdrasilInventoryPlugin.getInstance().getManager(), ID, player);
        return res ? Event.Result.ALLOW : Event.Result.DENY;
    }

    public Event.Result backPage(Inventory inventory, Player player, ItemStack stack) {
        boolean res = InventoryHelper.prevPage(YggdrasilInventoryPlugin.getInstance().getManager(), ID, player);
        return res ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @ListItems
    public List<YggdrasilItem> getListItems() {
        List<YggdrasilItem> stack = Lists.newArrayList();
        for(int i = 0; i < 100; i++) {
            stack.add(new YggdrasilItemBuilder(Material.CYAN_WOOL).withName(Component.text("Item #" + i, TextColor.color(Color.CYAN.getRGB()))).build());
        }
        return stack;
    }

}
