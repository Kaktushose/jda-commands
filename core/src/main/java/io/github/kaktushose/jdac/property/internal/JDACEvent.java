package io.github.kaktushose.jdac.property.internal;

import dev.goldmensch.propane.event.Event;
import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscriber;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.events.*;

/// A [FrameworkEvent] is published at multiple points during the lifespan of the framework. It is always created by the framework
/// and can be [subscribed to][Introspection#subscribe(Class, Subscriber)] by users, who can provide own logic to run when
/// a certain event is published.
///
/// The currently available events are:
///
/// - [FrameworkShutdownEvent] - fired in [JDACommands#shutdown()]
/// - [FrameworkStartEvent] - fired in [JDACBuilder#start()]
/// - [InteractionStartEvent] - fired before executing the interaction controller method
/// - [InteractionFinishedEvent] - fired after executing the interaction controller method
/// - [RuntimeOpenEvent] - fired at the start of a new interaction, e.g. when executing a slash command
/// - [RuntimeCloseEvent] - fired at the end of a user interaction, e.g. due to inactivity
///
/// For further information on each event visit the corresponding javadoc.
public interface JDACEvent extends Event<JDACScope> {
  default JDACScope scope() {
    return Registry.INSTANCE.scopeForEvent(this.getClass());
  }
}
