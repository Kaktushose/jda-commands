package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A {@link Middleware} implementation that will check the parameter constraints a
 * {@link SlashCommandDefinition} might have.
 *
 * @see com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry ValidatorRegistry
 * @since 2.0.0
 */
public class ConstraintMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(ConstraintMiddleware.class);

    /**
     * Checks if all parameters fulfill their constraints. Will cancel the {@link Context} if a parameter
     * constraint fails.
     *
     * @param ctx the {@link Context} to filter
     */
    @Override
    public void accept(@NotNull ExecutionContext<?, ?> ctx) {
        if (!(ctx instanceof CommandExecutionContext<?,?> context) || !(context.interactionDefinition() instanceof SlashCommandDefinition command))
            return;

        List<Object> arguments = context.arguments();
        List<ParameterDefinition> parameters = command.getParameters();

        log.debug("Applying parameter constraints...");
        for (int i = 1; i < arguments.size(); i++) {
            Object argument = arguments.get(i);
            ParameterDefinition parameter = parameters.get(i);
            for (ConstraintDefinition constraint : parameter.constraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, parameter.type().getName());

                boolean validated = constraint.validator().apply(argument, constraint.annotation(), context);

                if (!validated) {
                    context.cancel(
                            context.implementationRegistry()
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
