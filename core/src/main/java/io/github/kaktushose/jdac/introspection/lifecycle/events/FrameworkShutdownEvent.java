package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.lifecycle.Event;

/// Published when the framework is shutdown, practically at the start of [JDACommands#shutdown()].
@IntrospectionAccess(Stage.INITIALIZED)
public record FrameworkShutdownEvent() implements Event {
}
