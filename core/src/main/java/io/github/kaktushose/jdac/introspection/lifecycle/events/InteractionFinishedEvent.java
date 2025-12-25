package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.introspection.lifecycle.Event;
import org.jspecify.annotations.Nullable;

public record InteractionFinishedEvent(InvocationContext<?> invocationContext, @Nullable Exception exception) implements Event {
}
