package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with StringSelectMenu will be registered as a StringSelectMenu at startup.
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link Interaction}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type
 * {@link ComponentEvent SelectMenuEvent}</li>
 * </ul>
 *
 * @see Interaction
 * @see SelectOption
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringSelectMenu {

    /**
     * Configure the placeholder which is displayed when no selections have been made yet.
     *
     * @return the placeholder which is displayed when no selections have been made yet
     */
    String value() default "";

    /**
     * The minimum amount of values a user has to select.
     * <br>Default: {@code 1}
     *
     * <p>The minimum must not exceed the amount of available options.
     *
     * @return the minimum amount of values a user has to select
     */
    int minValue() default 1;

    /**
     * The maximum amount of values a user can select.
     * <br>Default: {@code 1}
     *
     * <p>The maximum must not exceed the amount of available options.
     *
     * @return the maximum amount of values a user can select
     */
    int maxValue() default 1;

    /**
     * Whether this button should send ephemeral replies by default.
     *
     * @return {@code true} if to send ephemeral replies
     */
    boolean ephemeral() default false;
}
