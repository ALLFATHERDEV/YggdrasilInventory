package net.odinallfather.yggdrasil.example;

import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.core.YggdrasilItem;
import net.odinallfather.yggdrasil.core.annotation.DynamicTitle;
import net.odinallfather.yggdrasil.core.annotation.Item;
import net.odinallfather.yggdrasil.core.annotation.YggdrasilInventory;
import net.odinallfather.yggdrasil.util.YggdrasilItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@YggdrasilInventory(size = 27, id = TestDynamicInventory.ID)
public class TestDynamicInventory {

    public static final String ID = "test_dynamic";


    @Item(slot = 0)
    public final YggdrasilItem item = YggdrasilItemBuilder.buildDynamic(Material.CYAN_WOOL, (player, itemStackBuilder) -> itemStackBuilder.withName(Component.text(player.getName())).build());

    @DynamicTitle
    public String getDynamicTitle(Player player) {
        return "Yeay: " + player.getName();
    }

}
