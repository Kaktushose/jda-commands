package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import net.dv8tion.jda.api.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConstraintFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ConstraintFilter.class);

    @Override
    public void apply(CommandContext context) {
        List<Object> arguments = context.getArguments();
        List<ParameterDefinition> parameters = context.getCommand().getParameters();

        log.debug("Applying parameter constraints...");
        for (int i = 1; i < arguments.size(); i++) {
            Object argument = arguments.get(i);
            ParameterDefinition parameter = parameters.get(i);
            for (ConstraintDefinition constraint : parameter.getConstraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, parameter.getType().getName());

                boolean validated = constraint.getValidator().validate(argument, constraint.getAnnotation(), context);

                if (!validated) {
                    context.setCancelled(true);
                    context.setErrorMessage(new MessageBuilder().append(constraint.getMessage()).build());
                    log.debug("Constraint failed!");
                    return;
                }
            }
        }
        log.debug("All constraints passed");
    }
}
