package com.github.kaktushose.jda.commands.definitions.reflect.misc;

import com.github.kaktushose.jda.commands.dispatching.validation.Validator;

/**
 * Representation of parameter constraint.
 *
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
 * @see Validator
 * @since 2.0.0
 */
public record ConstraintDefinition(Validator validator, String message, Object annotation) {
}
