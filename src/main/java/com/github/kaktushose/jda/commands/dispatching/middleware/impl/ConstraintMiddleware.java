package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

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
    public void execute(@NotNull Context ctx) {
        if (!SlashCommandInteractionEvent.class.isAssignableFrom(ctx.getEvent().getClass())) {
            return;
        }
        SlashCommandContext context = (SlashCommandContext) ctx;
        List<Object> arguments = context.getArguments();
        List<ParameterDefinition> parameters = Objects.requireNonNull(context.getCommand()).getParameters();

        log.debug("Applying parameter constraints...");
        for (int i = 1; i < arguments.size(); i++) {
            Object argument = arguments.get(i);
            ParameterDefinition parameter = parameters.get(i);
            for (ConstraintDefinition constraint : parameter.constraints()) {
                log.debug("Found constraint {} for parameter {}", constraint, parameter.type().getName());

                boolean validated = constraint.validator().validate(argument, constraint.annotation(), context);

                if (!validated) {
                    context.setCancelled(
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
