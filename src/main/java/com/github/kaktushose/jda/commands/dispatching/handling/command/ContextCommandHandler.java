package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;

import java.util.List;

public class ContextCommandHandler extends EventHandler<GenericContextInteractionEvent<?>, ExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition>> {

    public ContextCommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected ExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition> prepare(GenericContextInteractionEvent<?> event, Runtime runtime) {
        ContextCommandDefinition command = interactionRegistry.find(ContextCommandDefinition.class,
                it -> it.getName().equals(event.getFullCommandName()));

        ExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition> context = new ExecutionContext<>(
                event, command, runtime, handlerContext, List.of(), ctx -> new CommandEvent<>(ctx, interactionRegistry));

        context.arguments().addAll(List.of(
                event.getTarget(),
                new CommandEvent<>(context, interactionRegistry)
        ));

        return context;
    }
}
