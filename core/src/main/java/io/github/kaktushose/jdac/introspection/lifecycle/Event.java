package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.lifecycle.events.*;

/// A [Event] is published at multiple points during the lifespan of the framework. It is always created by the framework
/// and can be [subscribed to][Introspection#subscribe(Class, Subscriber)] by users, who can provide own logic to run when
/// a certain event is published.
public sealed interface Event permits FrameworkShutdownEvent, FrameworkStartEvent, InteractionFinishedEvent, InteractionStartEvent, RuntimeCloseEvent, RuntimeOpenEvent {
}
