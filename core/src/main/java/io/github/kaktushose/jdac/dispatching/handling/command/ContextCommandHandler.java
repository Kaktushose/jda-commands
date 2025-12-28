package io.github.kaktushose.jdac.dispatching.handling.command;

import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.ContextCommandDefinition;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public final class ContextCommandHandler extends EventHandler<GenericContextInteractionEvent<?>> {

    public ContextCommandHandler(IntrospectionImpl introspection) {
        super(introspection);
    }

    @Override
    protected PreparationResult prepare(GenericContextInteractionEvent<?> event, Runtime runtime) {
        CommandDefinition command = interactionRegistry.find(ContextCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        Object target = event.getTarget();
        if (event instanceof UserContextInteractionEvent userEvent) {
            target = userEvent.getTargetMember();
            if (target == null) {
                throw new InternalException("null-member-context-command");
            }
        }

        return new PreparationResult(command, List.of(new CommandEvent(), target));
    }
}
