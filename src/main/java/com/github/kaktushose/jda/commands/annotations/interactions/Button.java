package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.interactions.buttons.ButtonEvent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with Button will be registered as a button at startup.
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link Interaction}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type {@link ButtonEvent ButtonEvent}</li>
 * </ul>
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see Interaction
 * @since 2.3.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Button {

    /**
     * Gets the label of the button.
     *
     * @return the label of the button
     */
    String value() default "";

    /**
     * Gets the {@link ButtonStyle}.
     *
     * @return the {@link ButtonStyle}
     */
    ButtonStyle style() default ButtonStyle.PRIMARY;

    /**
     * Gets the {@link Emoji} of the button.
     *
     * @return the {@link Emoji}
     */
    String emoji() default "";

    /**
     * Gets the link of the button.
     *
     * @return the link of the button
     */
    String link() default "";

    /**
     * Whether this button should send ephemeral replies by default.
     *
     * @return {@code true} if to send ephemeral replies
     */
    boolean ephemeral() default false;

}
