package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.SequencedCollection;
import java.util.function.Function;

public record Invocation<T extends GenericInteractionCreateEvent>(
        InvocationContext<T> context,
        Function<GenericInteractionDefinition, Object> instanceSupplier,
        SequencedCollection<Object> arguments
) {
}
