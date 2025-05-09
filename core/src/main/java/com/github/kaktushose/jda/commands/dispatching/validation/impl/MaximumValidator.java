package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import org.jetbrains.annotations.NotNull;

/// A [Validator] implementation that checks the [Max] constraint.
///
/// @see Max
public class MaximumValidator implements Validator {

    /// Validates an argument. The argument must be a number whose value must be less or equal to the specified maximum.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument is a number whose value is less or equal to the specified maximum
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        Max max = (Max) annotation;
        return ((Number) argument).longValue() <= max.value();
    }
}
