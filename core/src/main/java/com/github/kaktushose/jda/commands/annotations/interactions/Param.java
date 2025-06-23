package com.github.kaktushose.jda.commands.annotations.interactions;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

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
    /// If the parameter class is an [Optional] this is automatically set to true.
    ///
    /// @return `true` if this command option is optional
    /// @see Command
    boolean optional() default false;

    /// Returns the [OptionType] of this command option.
    ///
    /// @return the [OptionType] of this command option
    /// @implNote If [OptionType#UNKNOWN] is passed (default value), jda-commands will interpolate the best fitting
    /// [OptionType], resulting to [OptionType#STRING] as a fallback.
    OptionType type() default OptionType.UNKNOWN;
}
