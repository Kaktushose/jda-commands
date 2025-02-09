package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.extension.Implementation;
import org.jetbrains.annotations.NotNull;

/// Validators checks if a command option fulfills the given constraint.
///
/// Either register them at the [JDACBuilder#validator(Class, Validator)()] or use the [Implementation] annotation.
///
/// @see Constraint
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
