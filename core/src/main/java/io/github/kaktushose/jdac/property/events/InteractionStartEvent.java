package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.property.JDACScope;

/// Published before an interaction controller method is invoked.
///
/// @param invocationContext the [InvocationContext] used to invoke this method
@IntrospectionAccess(JDACScope.INTERACTION)
public record InteractionStartEvent(InvocationContext<?> invocationContext) implements JDACEvent {
}
