package com.github.kaktushose.jda.commands.annotations.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.MenuOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal wrapper for repeating {@link MenuOption SelectOptions}.
 *
 * @see MenuOption
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectOptionContainer {

    MenuOption[] value();

}
