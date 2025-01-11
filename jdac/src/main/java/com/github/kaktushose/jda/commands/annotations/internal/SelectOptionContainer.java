package com.github.kaktushose.jda.commands.annotations.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.SelectOption;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Internal wrapper for repeating [SelectOption]s.
///
/// @see SelectOption
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Internal
public @interface SelectOptionContainer {

    SelectOption[] value();

}
