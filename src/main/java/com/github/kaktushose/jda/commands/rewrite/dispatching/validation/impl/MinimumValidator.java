package com.github.kaktushose.jda.commands.rewrite.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.rewrite.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.Validator;

public class MinimumValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Min min = (Min) annotation;
        return ((Number) argument).longValue() >= min.value();
    }
}
