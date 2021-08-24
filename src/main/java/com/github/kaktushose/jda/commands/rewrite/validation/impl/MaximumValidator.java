package com.github.kaktushose.jda.commands.rewrite.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.rewrite.validation.Validator;
import net.dv8tion.jda.api.entities.Guild;

public class MaximumValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, Guild guild) {
        Max max = (Max) annotation;
        return ((Number) argument).longValue() <= max.value();
    }
}
