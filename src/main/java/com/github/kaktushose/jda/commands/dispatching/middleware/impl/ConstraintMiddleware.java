package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.definitions.reflect.misc.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.definitions.reflect.misc.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.commands.SlashCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    private final ImplementationRegistry implementationRegistry;

    public ConstraintMiddleware(ImplementationRegistry implementationRegistry) {
        this.implementationRegistry = implementationRegistry;
    }

    /**
     * Checks if all parameters fulfill their constraints. Will cancel the {@link InvocationContext} if a parameter
     * constraint fails.
     *
     * @param context the {@link InvocationContext} to filter
     */
    @Override
    public void accept(@NotNull InvocationContext<?> context) {
        if (!(context.definition() instanceof SlashCommandDefinition command))
            return;

        var arguments = new ArrayList<>(context.arguments());
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
                            implementationRegistry
                                    .getErrorMessageFactory()
                                    .getConstraintFailedMessage(constraint)
                    );
                    log.debug("Constraint failed!");
                    return;
                }
            }
        }
        log.debug("All constraints passed");
    }
}
