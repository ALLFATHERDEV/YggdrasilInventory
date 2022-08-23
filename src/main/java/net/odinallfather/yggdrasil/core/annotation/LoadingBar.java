package net.odinallfather.yggdrasil.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you want to set a loading bar with "animation" use this annotation above a method with the following structure:
 *
 * <code>
 *      public void testLoadingBar(Inventory inventory, Player player, ItemStack item) {
 *
 *     }
 * </code>
 *
 * This method will be called when the loading bar is finished
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoadingBar {

    /**
     * @return the start position
     */
    int startPos();

    /**
     * @return the width must be a number between 1 and 8
     */
    int width();

    /**
     * @return the item id for the loading bar item
     */
    String loadingBarItemId();

    /**
     * @return the animation fill type
     */
    FillType fillType() default FillType.EMPTY_TO_FULL;

    /**
     * @return the ticks where the loading bar will fill the bar
     */
    int tick() default 40;

    enum FillType {

        /**
         * The bar fills the inventory row
         */
        EMPTY_TO_FULL,

        /**
         * The inventory row is already filled, but when the bar loads the items get replaced
         * i.e: red wool filled, when the bar loads the red wool will be placed with green wool
         */
        FULL_TO_FULL


    }

}
