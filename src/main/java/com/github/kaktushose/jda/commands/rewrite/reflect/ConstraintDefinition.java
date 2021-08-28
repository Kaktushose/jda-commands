package com.github.kaktushose.jda.commands.rewrite.reflect;

import com.github.kaktushose.jda.commands.rewrite.validation.Validator;

public class ConstraintDefinition {

    private final Validator validator;
    private final String message;

    public ConstraintDefinition(Validator validator, String message) {
        this.validator = validator;
        this.message = message;
    }

    public Validator getValidator() {
        return validator;
    }

    public String getMessage() {
        return message;
    }
}
