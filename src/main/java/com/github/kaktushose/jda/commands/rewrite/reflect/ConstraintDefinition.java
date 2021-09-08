package com.github.kaktushose.jda.commands.rewrite.reflect;

import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.Validator;

public class ConstraintDefinition {

    private final Validator validator;
    private final String message;
    private final Object annotation;

    public ConstraintDefinition(Validator validator, String message, Object annotation) {
        this.validator = validator;
        this.message = message;
        this.annotation = annotation;
    }

    public Validator getValidator() {
        return validator;
    }

    public String getMessage() {
        return message;
    }

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
