package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.introspection.lifecycle.events.*;

public sealed interface Event permits FrameworkShutdownEvent, FrameworkStartEvent, InteractionFinishedEvent, InteractionStartEvent, RuntimeCloseEvent, RuntimeOpenEvent {
}
