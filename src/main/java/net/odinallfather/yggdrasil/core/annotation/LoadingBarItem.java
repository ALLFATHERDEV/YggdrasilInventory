package net.odinallfather.yggdrasil.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this in combination with {@link LoadingBar}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LoadingBarItem {

    /**
     * @return the loading bar id
     */
    String id();

    /**
     * @return if this is set to true, then this item will be placed if the {@link net.odinallfather.yggdrasil.core.annotation.LoadingBar.FillType}
     * is set to {@link net.odinallfather.yggdrasil.core.annotation.LoadingBar.FillType#FULL_TO_FULL}
     */
    boolean filler() default false;

}
