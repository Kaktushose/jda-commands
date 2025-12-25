package io.github.kaktushose.jdac.introspection.internal;

import io.github.kaktushose.jdac.introspection.lifecycle.Event;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscriber;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscription;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Lifecycle {

    private final Map<Class<? extends Event>, Set<Subscriber<Event>>> subscriptions = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> Subscription subscribe(Class<T> event, Subscriber<T> subscriber) {
        subscriptions.computeIfAbsent(event, _ -> ConcurrentHashMap.newKeySet()).add((Subscriber<Event>) subscriber);
        return new Subscription(event, subscriber, this);
    }

    public void unsubscribe(Subscriber<?> subscriber, Class<? extends Event> eventType) {
        subscriptions.get(eventType).remove(subscriber);
    }

    public void publish(Event event, IntrospectionImpl introspection) {
        for (Subscriber<Event> subscriber : subscriptions.getOrDefault(event.getClass(), Set.of())) {
            subscriber.accept(event, introspection);
        }
    }
}
