package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.internal.Lifecycle;
import org.jetbrains.annotations.ApiStatus;

/// The [Subscription] representing a single subscription to a certain event.
/// Each call to [Introspection#subscribe(Class, Subscriber)] returns a new instance of this class.
public final class Subscription {

    private final Class<? extends FrameworkEvent> event;
    private final Lifecycle lifecycle;
    private final Subscriber<?> subscriber;

    @ApiStatus.Internal
    public Subscription(Class<? extends FrameworkEvent> event, Subscriber<?> subscriber, Lifecycle lifecycle) {
        this.event = event;
        this.lifecycle = lifecycle;
        this.subscriber = subscriber;
    }


    /// Terminates (unsubscribes) this [Subscription]. The associated [Subscriber] won't be called in the future.
    public void unsubscribe() {
        lifecycle.unsubscribe(subscriber, event);
    }
}
