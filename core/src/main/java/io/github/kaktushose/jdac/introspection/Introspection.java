package io.github.kaktushose.jdac.introspection;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import io.github.kaktushose.jdac.introspection.lifecycle.Event;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscriber;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscription;

public sealed interface Introspection permits IntrospectionImpl {
    static boolean accessible() {
        return EventHandler.INTROSPECTION.isBound();
    }

    static Introspection access() {
        return EventHandler.INTROSPECTION.get();
    }

    static <T> T accGet(Property<T> type) {
        return access().get(type);
    }

    Stage currentStage();

    <T> T get(Property<T> type);

    <T extends Event> Subscription subscribe(Class<T> event, Subscriber<T> subscriber);

}
