package io.github.kaktushose.jdac.introspection;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscriber;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscription;
import io.github.kaktushose.jdac.introspection.lifecycle.events.RuntimeCloseEvent;
import io.github.kaktushose.jdac.message.MessageResolver;

import java.util.NoSuchElementException;


/// The introspection api provides read-only access to JDA-Commands [property system][Property] and allows
/// subscribing to [FrameworkEvent]s in the frameworks interaction execution process.
///
/// To access the api, you can either
/// - use [JDACommands#introspection()] or [Event#introspection()]
/// - use [Introspection#access()] that will return the current instance of this scope
///
/// When accessing trough [Introspection#access()], you have to pay attention where you do so.
/// An [Introspection] instance is set in most but not in all places, to know where you can use it take a look at the
/// [IntrospectionAccess] annotation on user implementable methods provided by the framework.
///
/// Inside of interaction controller methods
/// (the ones having [ComponentEvent], [CommandEvent] etc. as a parameter and are defined inside a class annotated with [Interaction] )
/// the [Introspection] instance is always set with the stage [Stage#INTERACTION], providing access to all [Property]s.
///
/// ## [Stage] and [Properties][Property] access
/// The [Property] API allows accessing all public components and configuration options of JDA-Commands.
/// It can be used to retrieve framework services like [MessageResolver], config options like [Property#GLOBAL_REPLY_CONFIG]
/// or context dependent information like [InvocationContext].
///
/// Please note that access is read-only, you can't set the value of a [Property] after starting the framework.
///
/// Based on the location (in code) where you access this api, the available [Properties][Property] may very.
/// To know what property is accessible, take a look at [Introspection#currentStage()] and compare it to [Property#stage()].
/// A hint on the current stage is also provided by [IntrospectionAccess#value()].
///
/// ## [FrameworkEvent]s
/// Sometimes it's convenient to execute some custom code at some point during runtime based on events inside the framework.
///
/// An example can be found inside the guice extension, were we use a [RuntimeCloseEvent] to remove
/// the interaction controller instances inside the cache at the end of a conversation.
///
/// To subscribe to a [FrameworkEvent] you use [#subscribe(Class, Subscriber)] which returns a [Subscription] allowing you
/// to "unsubscribe" from this event later. If an [FrameworkEvent] is fired by JDA-Commands all [Subscriber]s of that event
/// are called.
///
/// It's important to know that the events are published by multiple threads perhaps concurrently, thus [Subscriber]s
/// may be also called concurrently. They have to be written with threadsafety in mind!
///
/// @implNote Internally [Introspection#access()] uses [ScopedValue]s.
///           For further clarification on how this works with [Thread]s take a look there.
public sealed interface Introspection permits IntrospectionImpl {

    /// Checks whether an [Introspection] instance can be access by [Introspection#access()]
    ///
    /// @return `true` when you can use [Introspection#access()], `false` when not.
    /// @see ScopedValue#isBound()
    static boolean accessible() {
        return IntrospectionImpl.INTROSPECTION.isBound();
    }

    /// Gets the current [Introspection] instance of this scope if set.
    /// For further information on when this is available take a look at the class docs.
    ///
    /// The available properties are based on the [Stage] of the current instance, you can
    /// check the currently available stage via  [Introspection#currentStage()].
    ///
    /// @throws NoSuchElementException if no [Introspection] instance is set
    /// @see ScopedValue#get()
    static Introspection access() {
        return IntrospectionImpl.INTROSPECTION.get();
    }


    /// Shorthand for `Introspection.access().get(Property)`.
    ///
    /// This method is scope dependent, see [#access()].
    ///
    /// @param property the requested [Property]
    /// @return the property's value
    /// @throws NoSuchElementException if no [Introspection] instance is set
    ///
    /// @see Introspection#access()
    /// @see Introspection#get(Property)
    static <T> T accGet(Property<T> property) {
        return access().get(property);
    }

    /// Returns the current [Stage] of this introspection instance.
    /// To know which properties are available take a look at the docs of [Stage] and [Property].
    ///
    /// @return the current [Stage]
    Stage currentStage();

    /// Gets the value of a property. At that point practically all properties should be resolved and caches,
    /// you can expect nearly instant access time.
    ///
    /// To exactly know what this does take a look at [PropertyProvider] and [PropertyProvider.Context#get(Property)].
    ///
    /// @param property the requested [Property]
    /// @return the property's value
    <T> T get(Property<T> property);

    /// Subscribes to specific [FrameworkEvent] allowing you to execute custom logic at multiple points during runtime.
    ///
    /// @param event the concrete class of [FrameworkEvent], e.g. [`RuntimeCloseEvent.class`][RuntimeCloseEvent#getClass()]
    /// @param subscriber the [Subscriber] holding the logic to be executed
    ///
    /// @return the [Subscription] instance as a unique reference to your subscription, allowing you to later cancel it.
    <T extends FrameworkEvent> Subscription subscribe(Class<T> event, Subscriber<T> subscriber);

}
