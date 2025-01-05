package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.definitions.interactions.command.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/// A [Middleware] implementation that will check the parameter constraints a
/// [SlashCommandDefinition] might have.
///
/// @see com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry ValidatorRegistry
public class ConstraintMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(ConstraintMiddleware.class);

    private final ImplementationRegistry implementationRegistry;

    public ConstraintMiddleware(ImplementationRegistry implementationRegistry) {
        this.implementationRegistry = implementationRegistry;
    }

    /// Checks if all parameters fulfill their constraints. Will cancel the [InvocationContext] if a parameter
    /// constraint fails.
    ///
    /// @param context the [InvocationContext] to filter
    @Override
    public void accept(@NotNull InvocationContext<?> context) {
        if (!(context.definition() instanceof SlashCommandDefinition command))
            return;

        var arguments = new ArrayList<>(context.arguments());
        List<ParameterDefinition> parameters = List.copyOf(command.commandParameters());

        log.debug("Applying parameter constraints...");
        for (int i = 1; i < arguments.size(); i++) {
            Object argument = arguments.get(i);
            ParameterDefinition parameter = parameters.get(i);
            for (ParameterDefinition.ConstraintDefinition constraint : parameter.constraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, parameter.type().getName());

                boolean validated = constraint.validator().apply(argument, constraint.annotation(), context);

                if (!validated) {
                    context.cancel(
                            implementationRegistry
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
