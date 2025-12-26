package io.github.kaktushose.jdac.introspection.internal;

import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscriber;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscription;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class Lifecycle {

    private final Map<Class<? extends FrameworkEvent>, Set<Subscriber<FrameworkEvent>>> subscriptions = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends FrameworkEvent> Subscription subscribe(Class<T> event, Subscriber<T> subscriber) {
        subscriptions.computeIfAbsent(event, _ -> ConcurrentHashMap.newKeySet()).add((Subscriber<@NonNull FrameworkEvent>) subscriber);
        return new Subscription(event, subscriber, this);
    }

    public void unsubscribe(Subscriber<?> subscriber, Class<? extends FrameworkEvent> eventType) {
        subscriptions.get(eventType).remove(subscriber);
    }

    public void publish(FrameworkEvent event, IntrospectionImpl introspection) {
        ScopedValue.where(IntrospectionImpl.INTROSPECTION, introspection).run(() -> {
            for (Subscriber<FrameworkEvent> subscriber : subscriptions.getOrDefault(event.getClass(), Set.of())) {
                subscriber.accept(event, introspection);
            }
        });
    }
}
