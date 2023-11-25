package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.annotations.internal.SelectOptionContainer;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.lang.annotation.*;

/**
 * Used to define {@link net.dv8tion.jda.api.interactions.components.selections.SelectOption SelectOptions}
 * for {@link StringSelectMenu StringSelectMenus}.
 *
 * @see Interaction
 * @see StringSelectMenu
 * @since 4.0.0
 */
@Repeatable(SelectOptionContainer.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectOption {

    /**
     * Gets the label of an option.
     *
     * @return the label
     */
    String label();

    /**
     * Gets the value of an option.
     *
     * @return the value
     */
    String value();

    /**
     * Gets the description of an option.
     *
     * @return the description
     */
    String description() default "";

    /**
     * Gets the {@link Emoji} of an option.
     *
     * @return the {@link Emoji}
     */
    String emoji() default "";

    /**
     * Gets whether this option is a default option.
     *
     * @return {@code true} if this SelectOption is a default option
     */
    boolean isDefault() default false;

}
