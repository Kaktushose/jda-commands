package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;

/**
 * A {@link Validator} implementation that checks the {@link Min} constraint.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Min
 * @since 2.0.0
 */
public class MinimumValidator implements Validator {

    /**
     * Validates an argument. The argument must be a number whose value must be greater or equal to the specified
     * minimum.
     *
     * @param argument the argument to validate
     * @param annotation the corresponding annotation
     * @param context the corresponding {@link CommandContext}
     * @return {@code true} if the argument is a number whose value is greater or equal to the specified minimum
     */
    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Min min = (Min) annotation;
        return ((Number) argument).longValue() >= min.value();
    }
}
