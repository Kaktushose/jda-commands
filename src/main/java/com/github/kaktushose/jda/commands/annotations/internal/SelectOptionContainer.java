package com.github.kaktushose.jda.commands.annotations.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.SelectOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal wrapper for repeating {@link SelectOption SelectOptions}.
 *
 * @see SelectOption
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectOptionContainer {

    SelectOption[] value();

}
