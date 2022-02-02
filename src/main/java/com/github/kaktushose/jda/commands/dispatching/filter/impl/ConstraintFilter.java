package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A {@link Filter} implementation that will check the parameter constraints a
 * {@link com.github.kaktushose.jda.commands.reflect.CommandDefinition} might have.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry ValidatorRegistry
 * @since 2.0.0
 */
public class ConstraintFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ConstraintFilter.class);

    /**
     * Checks if all parameters fulfill their constraints. Will cancel the {@link CommandContext} if a parameter
     * constraint fails.
     *
     * @param context the {@link CommandContext} to filter
     */
    @Override
    public void apply(@NotNull CommandContext context) {
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
                    context.setErrorMessage(
                            context.getImplementationRegistry()
                                    .getErrorMessageFactory()
                                    .getConstraintFailedMessage(context, constraint)
                    );
                    log.debug("Constraint failed!");
                    return;
                }
            }
        }
        log.debug("All constraints passed");
    }
}
