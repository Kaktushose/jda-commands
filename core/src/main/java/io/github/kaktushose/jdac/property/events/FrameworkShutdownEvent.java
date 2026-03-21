package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.internal.JDACEvent;

/// Published when the framework is shutdown, practically at the start of [JDACommands#shutdown()].
@IntrospectionAccess(JDACScope.INITIALIZED)
public record FrameworkShutdownEvent() implements JDACEvent {
}
