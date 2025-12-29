package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;

/// Published when the framework is fully started, practically at the end of [JDACBuilder#start()].
@IntrospectionAccess(Stage.INITIALIZED)
public record FrameworkStartEvent() implements FrameworkEvent {
}
