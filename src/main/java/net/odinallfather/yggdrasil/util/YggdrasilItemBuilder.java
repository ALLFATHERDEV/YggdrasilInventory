package net.odinallfather.yggdrasil.util;

import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import net.odinallfather.yggdrasil.core.YggdrasilItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class YggdrasilItemBuilder {

    private Material material;
    private Component name;
    private int amount = 1;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;

    private String junitTestName;

    public YggdrasilItemBuilder(Material material) {
        this.material = material;
    }


    public YggdrasilItemBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    public YggdrasilItemBuilder withName(Component name) {
        this.name = name;
        return this;
    }

    public YggdrasilItemBuilder withJUnitTestName(String name) {
        this.junitTestName = name;
        return this;
    }

    public YggdrasilItemBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public YggdrasilItemBuilder withLore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public YggdrasilItemBuilder withLore(Component lore) {
        if (this.lore == null)
            this.lore = new ArrayList<>();
        this.lore.add(lore);
        return this;
    }

    public YggdrasilItemBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public YggdrasilItemBuilder withEnchantment(Enchantment enchantment, int level) {
        if (this.enchantments == null)
            this.enchantments = new HashMap<>();
        this.enchantments.put(enchantment, level);
        return this;
    }

    public YggdrasilItem build() {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        if (name != null)
            if (YggdrasilInventoryPlugin.isJUnitTest()) {
                meta.setDisplayName(junitTestName);
            } else
                meta.displayName(name);
        if (lore != null && !YggdrasilInventoryPlugin.isJUnitTest())
            meta.lore(lore);
        if (enchantments != null)
            stack.addEnchantments(this.enchantments);
        stack.setItemMeta(meta);
        return player -> stack;
    }

    public static YggdrasilItem buildDynamic(Material material, BiFunction<Player, YggdrasilItemBuilder, YggdrasilItem> builder) {
        return player -> builder.apply(player, new YggdrasilItemBuilder(material)).get(player);
    }


}
