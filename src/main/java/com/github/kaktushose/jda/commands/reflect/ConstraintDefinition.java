package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import org.jetbrains.annotations.NotNull;

/**
 * Representation of parameter constraint.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Constraint Constraint
 * @see Validator
 * @since 2.0.0
 */
public class ConstraintDefinition {

    private final Validator validator;
    private final String message;
    private final Object annotation;

    /**
     * Constructs a new ConstraintDefinition.
     *
     * @param validator  the {@link Validator} to use
     * @param message    the message to display if the constraint fails
     * @param annotation an instance of the annotation declaring the constraint
     */
    public ConstraintDefinition(@NotNull Validator validator, @NotNull String message, @NotNull Object annotation) {
        this.validator = validator;
        this.message = message;
        this.annotation = annotation;
    }

    /**
     * Gets the {@link Validator}.
     *
     * @return the {@link Validator}
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * Gets the message to display if the constraint fails.
     *
     * @return the message to display if the constraint fails
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets an instance of the annotation declaring the constraint.
     *
     * @return an instance of the annotation declaring the constraint
     */
    public Object getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return "{" +
                "validator=" + validator.getClass().getName() +
                ", message='" + message + "'}";
    }
}
