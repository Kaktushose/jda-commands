package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.definitions.description.Invoker;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.property.JDACScope;
import org.jspecify.annotations.Nullable;

import java.util.SequencedCollection;

/// Published after an interaction controller method is called.
///
/// @param invocationContext the [InvocationContext] that was used to invoke the interaction controller method
/// @param exception         the [Exception] thrown by [Invoker#invoke(Object, SequencedCollection)] or `null` if no exception was thrown
@IntrospectionAccess(JDACScope.INTERACTION)
public record InteractionFinishedEvent(
        InvocationContext<?> invocationContext,
        @Nullable Exception exception
) implements JDACEvent {
}
