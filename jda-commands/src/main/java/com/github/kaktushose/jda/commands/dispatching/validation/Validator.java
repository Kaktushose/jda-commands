package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.jetbrains.annotations.NotNull;

/// Generic top level interface for validators. A validator checks if a command argument fulfills the given constraint.
///
/// @see com.github.kaktushose.jda.commands.annotations.Implementation Implementation
/// @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
@FunctionalInterface
public interface Validator {

    /// Validates an argument.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument passes the constraints
    boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context);

}
