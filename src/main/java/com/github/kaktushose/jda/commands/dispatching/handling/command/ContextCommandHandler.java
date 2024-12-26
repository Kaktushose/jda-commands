package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
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
        CommandDefinition command = registry.find(CommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        return new InvocationContext<>(event, runtime.keyValueStore(), command,
                List.of(new CommandEvent(event, registry, runtime, command), event.getTarget())
        );
    }
}
