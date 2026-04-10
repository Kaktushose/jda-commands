package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.JDACScope;

/// Published when the framework is fully started, practically at the end of [JDACBuilder#start()].
@IntrospectionAccess(JDACScope.INITIALIZED)
public record FrameworkStartEvent() implements JDACEvent {
}
