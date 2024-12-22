package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
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
        ContextCommandDefinition command = interactionRegistry.find(ContextCommandDefinition.class, true, it ->
                it.getName().equals(event.getFullCommandName())
        );

        return new InvocationContext<>(event, runtime.keyValueStore(), command,
                List.of(new CommandEvent(event, interactionRegistry, runtime, command), event.getTarget())
        );
    }
}
