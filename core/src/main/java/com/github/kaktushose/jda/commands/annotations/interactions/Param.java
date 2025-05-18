package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation used to add metadata, e.g. a description, to command options.
///
/// @see Command
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    /// Returns the description of the command option. This value will only be used for slash commands.
    ///
    /// @return the description of the command option
    String value() default "";

    /// Returns the name of the command option. Use the compiler flag `-parameters` to have the parameter name resolved
    /// at runtime making this field redundant.
    ///
    /// @return the name of the command option
    String name() default "";

    /// Returns whether this command option is optional.
    ///
    ///  @return `true` if this command option is optional
    boolean optional() default false;

    /// Returns the fallback value to use if this command option is optional and no value was passed. The default value
    /// will be handled as a normal String input and thus the [TypeAdapters] will try to parse it.
    /// If the parsing fails the command will still be executed but with empty or possible `null` values.
    ///
    /// @return the default value
    String fallback() default "";

}
