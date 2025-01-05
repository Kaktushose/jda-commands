package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapterRegistry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Parameters annotated with Optional are as the name says optional.
///
/// More formally if a command has an optional parameter the argument doesn't need to be present to execute the
/// command.
///
/// It is also possible to pass a default value which will be used instead if the argument isn't present.
/// The default value will be handled as a normal input and thus the [TypeAdapterRegistry] will try to parse it.
/// If the parsing fails the command will still be executed but with empty or possible `null` values.
///
/// @see SlashCommand
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {

    /// Returns the default value.
    ///
    /// @return the default value
    String value() default "";
}
