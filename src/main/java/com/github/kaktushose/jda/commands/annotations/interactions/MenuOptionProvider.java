package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this method is used to provide
 * {@link net.dv8tion.jda.api.interactions.components.selections.SelectOption SelectOptions} for a
 * {@link StringSelectMenu}
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link Interaction}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type
 * {@link ComponentEvent SelectMenuEvent}</li>
 * </ul>
 *
 * @see DynamicOptions
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuOptionProvider {
}
