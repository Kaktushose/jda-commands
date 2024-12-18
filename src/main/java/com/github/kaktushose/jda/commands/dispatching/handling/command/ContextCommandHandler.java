package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;

import java.util.List;

public final class ContextCommandHandler extends EventHandler<GenericContextInteractionEvent<?>> {

    public ContextCommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected InvocationContext<GenericContextInteractionEvent<?>> prepare(GenericContextInteractionEvent<?> event, Runtime runtime) {
        ContextCommandDefinition command = interactionRegistry.find(ContextCommandDefinition.class, true, it ->
                it.getName().equals(event.getFullCommandName())
        );

        return newContext(event, runtime, command,
                List.of(new CommandEvent(event, interactionRegistry, runtime, command.replyConfig()), event.getTarget())
        );
    }
}
