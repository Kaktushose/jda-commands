package com.github.kaktushose.jda.commands.rewrite.validation.impl;

import com.github.kaktushose.jda.commands.rewrite.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.validation.Validator;

public class MaximumValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Max max = (Max) annotation;
        return ((Number) argument).longValue() <= max.value();
    }
}
