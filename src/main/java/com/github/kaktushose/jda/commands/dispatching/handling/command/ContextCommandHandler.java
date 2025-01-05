package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public final class ContextCommandHandler extends EventHandler<GenericContextInteractionEvent<?>> {

    public ContextCommandHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<GenericContextInteractionEvent<?>> prepare(@NotNull GenericContextInteractionEvent<?> event, @NotNull Runtime runtime) {
        CommandDefinition command = registry.find(ContextCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        return new InvocationContext<>(event, runtime.keyValueStore(), command,
                List.of(new CommandEvent(event, registry, runtime, command, dispatchingContext.globalReplyConfig()), event.getTarget())
        );
    }
}
