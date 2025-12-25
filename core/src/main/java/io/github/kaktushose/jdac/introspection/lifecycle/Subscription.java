package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.introspection.internal.Lifecycle;

public class Subscription {

    private final Class<? extends Event> event;
    private final Lifecycle lifecycle;
    private final Subscriber<?> subscriber;

    public Subscription(Class<? extends Event> event, Subscriber<?> subscriber, Lifecycle lifecycle) {
        this.event = event;
        this.lifecycle = lifecycle;
        this.subscriber = subscriber;
    }


    public void unsubscribe() {
        lifecycle.unsubscribe(subscriber, event);
    }
}
