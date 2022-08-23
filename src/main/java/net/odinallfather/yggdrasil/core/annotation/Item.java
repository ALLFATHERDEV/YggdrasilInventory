package net.odinallfather.yggdrasil.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This represents an item in the inventory
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {

    /**
     * @return the slot where the item will be placed
     */
    int slot() default -1;

    /**
     * You can return multiple different slots in the inventory where the item will be placed
     * @return multiple slots for the item
     */
    int[] multipleSlots() default {};

    /**
     * If you want to place the item multiple times in a row then is this the min coordinate
     * @return the min pos
     */
    int slotRangeMin() default -1;

    /**
     * If you want to place the item multiple times in a row then is this the max coordinate
     * @return the max pos
     */
    int slotRangeMax() default -1;


}
