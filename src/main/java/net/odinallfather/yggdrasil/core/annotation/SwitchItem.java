package net.odinallfather.yggdrasil.core.annotation;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you have an item that switches his item type with every click then use this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SwitchItem {

    /**
     * Set the id so the parser can collect all the items that should be contained in this mode
     * @return the id
     */
    String id();

    /**
     * If you want to specify an order, fill this array with the item ids for the order
     * If it is empty, then the order will be like the items are listed in the class
     * @return the order
     */
    String[] order() default {};

    /**
     * NOTE:
     * This is an additional method that is called when the item is been clicked
     * The first method who gets called contains the switch logic and is set in
     * {@link net.odinallfather.yggdrasil.core.YggdrasilParser#handleInventoryClick(InventoryClickEvent)}
     *
     * @return the method that should be executed when the item is been clicked
     */
    String executableMethod() default "NO_METHOD";


}
