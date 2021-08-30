package com.github.kaktushose.jda.commands.rewrite.validation.impl;

import com.github.kaktushose.jda.commands.rewrite.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.validation.Validator;

public class MinimumValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Min min = (Min) annotation;
        return ((Number) argument).longValue() >= min.value();
    }
}
