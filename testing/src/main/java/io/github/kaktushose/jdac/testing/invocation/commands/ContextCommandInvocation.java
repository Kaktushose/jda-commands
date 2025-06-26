package io.github.kaktushose.jdac.testing.invocation.commands;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.internal.CommandInvocation;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction;

import static org.mockito.Mockito.*;

@SuppressWarnings("rawtypes")
public final class ContextCommandInvocation<T> extends CommandInvocation<GenericContextInteractionEvent> {

    public ContextCommandInvocation(Context context, String command, T target) {
        super(context, command, GenericContextInteractionEvent.class);

        lenient().when(event.getInteraction()).thenReturn(mock(ContextInteraction.class));

        when(event.getTarget()).thenReturn(target);
    }
}
