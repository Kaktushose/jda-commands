package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated select menu doesn't have static {@link MenuOption SelectOptions}, but instead the
 * options are provided by a {@link MenuOptionProvider}
 *
 * @see Interaction
 * @see StringSelectMenu
 * @see MenuOption
 * @since 4.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicOptions {

    /**
     * Gets the name of the {@link MenuOptionProvider} that should be used
     *
     * @return the name of the {@link MenuOptionProvider}
     */
    String value();

}
