package net.odinallfather.yggdrasil.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you want a text input from an anvil gui use this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TextInput {

    /**
     * This annotation need a special method
     * The method should look like this:
     *
     * <code>
     *     public AnvilGUI.Response onAnvilFinish(Player player, String text, Inventory oldInventory) {
     *         return AnvilGUI.Response.openInventory(oldInventory);
     *     }
     * </code>
     *
     * @return the name of the method that will be executed when the player finished his input
     */
    String methodName();

}
