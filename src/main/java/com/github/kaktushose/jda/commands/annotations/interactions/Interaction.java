package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with Interaction will be scanned at startup and are eligible for defining interactions such as
 * slash commands, buttons, modals or context menus.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interaction {

    /**
     * Returns the base name for slash commands.
     *
     * @return the base name for slash commands
     */
    String value() default "";

    /**
     * Returns whether this interaction is active and thus can be executed or not
     *
     * @return {@code true} if this interaction is active
     */
    boolean isActive() default true;

    /**
     * Returns whether this interaction should send ephemeral replies by default.
     *
     * @return {@code true} if this interaction should send ephemeral replies by default
     */
    boolean ephemeral() default true;

}
