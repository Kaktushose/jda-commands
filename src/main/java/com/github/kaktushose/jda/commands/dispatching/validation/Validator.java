package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import org.jetbrains.annotations.NotNull;

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
     * @param context    the corresponding {@link Context}
     * @return {@code true} if the argument passes the constraints
     */
    boolean validate(@NotNull Object argument, @NotNull Object annotation, @NotNull Context context);

}
