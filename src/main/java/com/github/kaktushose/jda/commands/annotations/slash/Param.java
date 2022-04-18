package com.github.kaktushose.jda.commands.annotations.slash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to add a name and description to parameters.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    /**
     * Returns the description of the parameter. This value will only be used for slash commands.
     *
     * @return the description of the parameter
     */
    String value() default "";

    /**
     * Returns the name of the parameter. Use the compiler flag {@code -parameters} to have the parameter name resolved
     * at runtime making this field redundant.
     *
     * @return the name of the parameter
     */
    String name() default "";

}
