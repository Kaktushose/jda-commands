package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.introspection.lifecycle.Event;

public record InteractionFinishedEvent(InvocationContext<?> invocationContext) implements Event {
}
