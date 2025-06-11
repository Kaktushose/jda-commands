package com.github.kaktushose.jda.commands.annotations.interactions;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Internal wrapper for repeating [MenuOption]s.
///
/// @see MenuOption
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Internal
public @interface MenuOptionContainer {

    MenuOption[] value();

}
