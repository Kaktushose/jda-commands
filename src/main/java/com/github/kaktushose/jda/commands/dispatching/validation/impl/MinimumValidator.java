package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import org.jetbrains.annotations.NotNull;

/// A [Validator] implementation that checks the [Min] constraint.
///
/// @see Min
public class MinimumValidator implements Validator {

    /// Validates an argument. The argument must be a number whose value must be greater or equal to the specified
    /// minimum.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument is a number whose value is greater or equal to the specified minimum
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        Min min = (Min) annotation;
        return ((Number) argument).longValue() >= min.value();
    }
}
