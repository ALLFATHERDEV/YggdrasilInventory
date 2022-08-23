package net.odinallfather.yggdrasil.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Only needed for list inventories.
 * If you have a list inventory you need to have this annotation above a method with the following structure
 *
 * <p></p>
 * <code>
 *     public List(ItemStack) methodName() {
 *         return list;
 *     }
 * </code>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ListItems {
}
