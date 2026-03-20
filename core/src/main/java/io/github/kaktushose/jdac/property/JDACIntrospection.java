package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.Introspection;
import dev.goldmensch.propane.event.Listener;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.property.events.RuntimeCloseEvent;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.property.internal.JDACEvent;
import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;

import java.util.NoSuchElementException;

/// The introspection api provides read-only access to JDA-Commands [property system][JDACProperty] and allows
/// subscribing to [JDACEvent]s in the frameworks interaction execution process.
///
/// To access the api, you can either
/// - use [JDACommands#introspection()] or [Event#introspection()]
/// - use [#accessScoped()] that will return the current instance of this scope
///
/// When accessing trough [#accessScoped()], you have to pay attention where you do so.
/// An [JDACIntrospection] instance is set in most but not in all places, to know where you can use it take a look at the
/// [IntrospectionAccess] annotation on user implementable methods provided by the framework.
///
/// Inside of interaction controller methods
/// (the ones having [ComponentEvent], [CommandEvent] etc. as a parameter and are defined inside a class annotated with [Interaction] )
/// the [JDACIntrospection] instance is always set with the stage [JDACScope#INTERACTION], providing access to all [JDACProperty]s.
///
/// ## [JDACScope] and [Properties][JDACProperty] access
/// The [JDACProperty] API allows accessing all public components and configuration options of JDA-Commands.
/// It can be used to retrieve framework services like [MessageResolver], config options like [JDACProperty#GLOBAL_REPLY_CONFIG]
/// or context dependent information like [InvocationContext].
///
/// Please note that access is read-only, you can't set the value of a [JDACProperty] after starting the framework.
///
/// Based on the location (in code) where you access this api, the available [Properties][JDACProperty] may very.
/// To know what property is accessible, take a look at [#currentStage()] and compare it to [JDACProperty#stage()].
/// A hint on the current stage is also provided by [IntrospectionAccess#value()].
///
/// ## [JDACEvent]s
/// Sometimes it's convenient to execute some custom code at some point during runtime based on events inside the framework.
///
/// An example can be found inside the guice extension, were we use a [RuntimeCloseEvent] to remove
/// the interaction controller instances inside the cache at the end of a conversation.
///
/// To subscribe to a [JDACEvent] you use [#subscribe(Class, Listener)] which returns a [Subscription] allowing you
/// to "unsubscribe" from this event later. If an [JDACEvent] is fired by JDA-Commands all [Listener]s of that event
/// are called.
///
/// It's important to know that the events are published by multiple threads perhaps concurrently, thus [Listener]s
/// may be also called concurrently. They have to be written with threadsafety in mind!
///
/// @implNote Internally [#accessScoped()] uses [ScopedValue]s.
///           For further clarification on how this works with [Thread]s take a look there.
public interface JDACIntrospection extends Introspection<JDACIntrospection, JDACScope> {

  /// Gets the value of a property. At that point practically all properties should be resolved and caches,
  /// you can expect nearly instant access time.
  ///
  /// To exactly know what this does take a look at [JDACPropertyProvider] and [JDACPropertyProvider.].
  ///
  /// @param property the requested [Property]
  /// @return the property's value
  <T> T get(JDACProperty<T> specific);

  /// Checks whether an [io.github.kaktushose.jdac.introspection.Introspection] instance can be access by [io.github.kaktushose.jdac.introspection.Introspection#accessScoped()]
  ///
  /// @return `true` when you can use [io.github.kaktushose.jdac.introspection.Introspection#accessScoped()], `false` when not.
  /// @see ScopedValue#isBound()
  static boolean accessible() {
    return JDACIntrospectionImpl.INTROSPECTION.isBound();
  }

  /// Gets the current [io.github.kaktushose.jdac.introspection.Introspection] instance of this scope if set.
  /// For further information on when this is available take a look at the class docs.
  ///
  /// The available properties are based on the [Stage] of the current instance, you can
  /// check the currently available stage via  [io.github.kaktushose.jdac.introspection.Introspection#currentStage()].
  ///
  /// @throws NoSuchElementException if no [io.github.kaktushose.jdac.introspection.Introspection] instance is set
  /// @see ScopedValue#get()
  static JDACIntrospection accessScoped() {
    return JDACIntrospectionImpl.INTROSPECTION.get();
  }

  /// Shorthand for `Introspection.access().get(Property)`.
  ///
  /// This method is scope dependent, see [#accessScoped()].
  ///
  /// @param property the requested [Property]
  /// @return the property's value
  /// @throws NoSuchElementException if no [io.github.kaktushose.jdac.introspection.Introspection] instance is set
  ///
  /// @see io.github.kaktushose.jdac.introspection.Introspection#accessScoped()
  /// @see io.github.kaktushose.jdac.introspection.Introspection#get(Property)
  static <T> T scopedGet(JDACProperty<T> property) {
    return accessScoped().get(property);
  }

  /// Subscribes to specific [FrameworkEvent] allowing you to execute custom logic at multiple points during runtime.
  ///
  /// @param event the concrete class of [FrameworkEvent], e.g. [`RuntimeCloseEvent.class`][RuntimeCloseEvent#getClass()]
  /// @param subscriber the [Subscriber] holding the logic to be executed
  ///
  /// @return the [Subscription] instance as a unique reference to your subscription, allowing you to later cancel it.
  @Override
  void subscribe(Listener<? extends dev.goldmensch.propane.event.Event<JDACScope>, JDACScope, JDACIntrospection> listener);
}
