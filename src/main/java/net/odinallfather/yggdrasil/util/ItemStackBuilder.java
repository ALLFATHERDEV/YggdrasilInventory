package net.odinallfather.yggdrasil.util;

import net.kyori.adventure.text.Component;
import net.odinallfather.yggdrasil.YggdrasilInventoryPlugin;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder {

    private final Material material;
    private Component name;
    private int amount = 1;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;

    private String junitTestName;

    public ItemStackBuilder(Material material) {
        this.material = material;
    }


    //===================================NORMAL===================================
    public ItemStackBuilder withName(Component name) {
        this.name = name;
        return this;
    }

    public ItemStackBuilder withJUnitTestName(String name) {
        this.junitTestName = name;
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder withLore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder withLore(Component lore) {
        if (this.lore == null)
            this.lore = new ArrayList<>();
        this.lore.add(lore);
        return this;
    }

    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, int level) {
        if (this.enchantments == null)
            this.enchantments = new HashMap<>();
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemStack build() {
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
        return stack;
    }


}
