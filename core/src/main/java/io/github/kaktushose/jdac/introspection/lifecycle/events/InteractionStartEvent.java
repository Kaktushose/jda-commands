package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;

/// Published before an interaction controller method is invoked.
///
/// @param invocationContext the [InvocationContext] used to invoke this method
@IntrospectionAccess(Stage.INTERACTION)
public record InteractionStartEvent(InvocationContext<?> invocationContext) implements FrameworkEvent {
}
