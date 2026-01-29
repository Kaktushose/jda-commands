package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;

import java.util.function.BiConsumer;

/// A [Subscriber] holds logic that will be executed when a certain [FrameworkEvent] is published by the framework.
///
/// @param <T> the subscribed to [FrameworkEvent]
/// @see Introspection#subscribe(Class, Subscriber)
public interface Subscriber<T extends FrameworkEvent> extends BiConsumer<T, Introspection> {

    /// Executed when the subscribed to [FrameworkEvent] ([T]) is published by the framework.
    ///
    /// Inside this method you can always use [Introspection#accessScoped()] and [Introspection#scopedGet(Property)],
    /// the returned [Introspection] instance is the one that was used to publish the event.
    /// Thus, the [stage][Introspection#currentStage()] of the [Introspection] is depending on the used
    ///  [FrameworkEvent].
    /// You can take a look at the specific [IntrospectionAccess] annotation of an [FrameworkEvent] class to know
    /// which stage is available.
    ///
    /// This method will be executed in different threads, thus the logic inside needs to be threadsafe!
    ///
    /// @param t             the published [FrameworkEvent]
    /// @param introspection the [Introspection] instance used to publish this event.
    @Override
    void accept(T t, Introspection introspection);
}
