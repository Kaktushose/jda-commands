package com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.rewrite.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.rewrite.reflect.ParameterDefinition;

import java.util.List;

public class ConstraintFilter implements Filter {

    @Override
    public void apply(CommandContext context) {
        List<Object> arguments = context.getArguments();
        List<ParameterDefinition> parameters = context.getCommand().getParameters();

        // we start with 1, so we don't go the CommandEvent
        for (int i = 1; i < parameters.size(); i++) {
            Object argument = arguments.get(i);
            ParameterDefinition parameter = parameters.get(i);

            for (ConstraintDefinition constraint : parameter.getConstraints()) {
                boolean validated = constraint.getValidator().validate(argument, constraint.getAnnotation(), context);

                if (!validated) {
                    context.setCancelled(true);
                    return;
                }
            }
        }

    }
}
