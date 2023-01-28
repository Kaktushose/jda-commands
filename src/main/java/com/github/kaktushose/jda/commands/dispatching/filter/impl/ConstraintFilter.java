package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * A {@link Filter} implementation that will check the parameter constraints a
 * {@link CommandDefinition} might have.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry ValidatorRegistry
 * @since 2.0.0
 */
public class ConstraintFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ConstraintFilter.class);

    /**
     * Checks if all parameters fulfill their constraints. Will cancel the {@link GenericContext} if a parameter
     * constraint fails.
     *
     * @param ctx the {@link GenericContext} to filter
     */
    @Override
    public void apply(@NotNull GenericContext ctx) {
        CommandContext context = (CommandContext) ctx;
        List<Object> arguments = context.getArguments();
        List<ParameterDefinition> parameters = Objects.requireNonNull(context.getCommand()).getParameters();

        log.debug("Applying parameter constraints...");
        for (int i = 1; i < arguments.size(); i++) {
            Object argument = arguments.get(i);
            ParameterDefinition parameter = parameters.get(i);
            for (ConstraintDefinition constraint : parameter.getConstraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, parameter.getType().getName());

                boolean validated = constraint.getValidator().validate(argument, constraint.getAnnotation(), context);

                if (!validated) {
                    context.setCancelled(true);
//                    context.setErrorMessage(
//                            context.getImplementationRegistry()
//                                    .getErrorMessageFactory()
//                                    .getConstraintFailedMessage(context, constraint)
//                    );
                    log.debug("Constraint failed!");
                    return;
                }
            }
        }
        log.debug("All constraints passed");
    }
}
