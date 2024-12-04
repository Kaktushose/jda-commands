package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with Interaction will be scanned at startup and are eligible for defining interactions such as
 * slash commands, buttonContainers, modals or context menus.
 *
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
     * Returns whether this interaction should send ephemeral replies by default.
     *
     * @return {@code true} if this interaction should send ephemeral replies by default
     */
    boolean ephemeral() default false;

}
