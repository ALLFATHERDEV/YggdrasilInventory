package net.odinallfather.yggdrasil.core.annotation;

import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * use this annotation to declare a class as an inventory
 * The plugin will scan all class files for this annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface YggdrasilInventory {

    /**
     * @return inventory size
     */
    int size();

    /**
     * @return title
     */
    String title() default  "";

    /**
     * @return id
     */
    String id();

    /**
     * @return true if you want an list inventory
     */
    boolean list() default false;

    /**
     * @return true if you want that the player can move items
     */
    boolean canMoveItems() default false;


}
