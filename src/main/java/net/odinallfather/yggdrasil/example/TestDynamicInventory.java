package net.odinallfather.yggdrasil.example;

import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.core.annotation.Item;
import net.odinallfather.yggdrasil.core.annotation.YggdrasilInventory;
import net.odinallfather.yggdrasil.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@YggdrasilInventory(size = 27, title = "Test", id = TestDynamicInventory.ID)
public class TestDynamicInventory {

    public static final String ID = "test_dynamic";

    private Player player;

    public TestDynamicInventory(Player player) {
        this.player = player;
    }

    @Item(slot = 0)
    public final ItemStack item = new ItemStackBuilder(Material.CYAN_WOOL).withName(Component.text(player.getName())).build();

}
