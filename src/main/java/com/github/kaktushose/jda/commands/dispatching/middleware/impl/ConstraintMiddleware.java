package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/// A [Middleware] implementation that will check the parameter constraints a [SlashCommandDefinition] might have.
///
/// @see com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators ValidatorRegistry
public class ConstraintMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(ConstraintMiddleware.class);

    private final ErrorMessageFactory errorMessageFactory;

    public ConstraintMiddleware(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = errorMessageFactory;
    }

    /// Checks if all parameters fulfill their constraints. Will cancel the [InvocationContext] if a parameter
    /// constraint fails.
    ///
    /// @param context the [InvocationContext] to filter
    @Override
    public void accept(@NotNull InvocationContext<?> context) {
        if (!(context.definition() instanceof SlashCommandDefinition command)) return;

        var arguments = new ArrayList<>(context.arguments());
        arguments.removeIf(Event.class::isInstance);
        var commandOptions = List.copyOf(command.commandOptions());

        log.debug("Applying parameter constraints...");
        for (int i = 0; i < arguments.size(); i++) {
            var argument = arguments.get(i);
            var optionData = commandOptions.get(i);
            for (var constraint : optionData.constraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, optionData.type().getName());
                if (!constraint.validator().apply(argument, constraint.annotation(), context)) {
                    context.cancel(errorMessageFactory.getConstraintFailedMessage(context, constraint));
                    log.debug("Constraint failed!");
                    return;
                }
            }
        }
        log.debug("All constraints passed");
    }
}
