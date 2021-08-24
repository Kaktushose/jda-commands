package com.github.kaktushose.jda.commands.rewrite.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.rewrite.validation.Validator;
import net.dv8tion.jda.api.entities.Guild;

public class MinimumValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, Guild guild) {
        Min min = (Min) annotation;
        return ((Number) argument).longValue() >= min.value();
    }
}
