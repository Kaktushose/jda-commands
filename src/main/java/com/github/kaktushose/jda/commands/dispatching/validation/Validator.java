package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;

/**
 * Generic top level interface for validators. A validator checks if a command argument fulfills the given constraint.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
 * @since 2.0.0
 */
public interface Validator {

    /**
     * Validates an argument.
     *
     * @param argument   the argument to validate
     * @param annotation the corresponding annotation
     * @param context    the corresponding {@link CommandContext}
     * @return {@code true} if the argument passes the constraints
     */
    boolean validate(Object argument, Object annotation, CommandContext context);

}
