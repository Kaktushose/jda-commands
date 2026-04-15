package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.IntrospectionSkeleton;
import dev.goldmensch.propane.Scope;
import dev.goldmensch.propane.event.Event;
import dev.goldmensch.propane.event.Listener;
import dev.goldmensch.propane.event.Subscription;
import dev.goldmensch.propane.property.*;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.events.JDACEvent;
import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;

import java.util.NoSuchElementException;

/// The [JDACIntrospection] type is the central element of the property system.
/// Its purpose is to expose the property and event system to the user.
///
/// Each [JDACIntrospection] instance is bound to a [Scope], allowing accessing the scopes' and its parents'
/// [Properties][Property]. For more information visit the documentation of [Scope]. Unless
/// the root scopes' [JDACIntrospection] instance, each [JDACIntrospection] instance is child of another, inheriting its values
/// that are combines with its own. For more information, visit the section [below](#properties)
///
/// ### Scopes access
/// Beside using [#get(SpecificProperty)] directly on an introspection instance, you can sometimes utilize
/// Java's [ScopedValue] API to get a properties value. For that to work, the [JDACIntrospection] class
/// features multiple static methods like [#scopedGet(JDACProperty)]. Methods using this scopes system, are always
/// prefixed with "scoped".
///
/// For further understanding, it is helpful to know how the [ScopedValue] API works.
/// In short: The [ScopedValue] API allows data to be available in a "context" whether only by field.
/// A context is for example the callchain in which you are, take following example:
///
/// ```java
/// static ScopedValue<JDACIntrospection> VAL = ScopedValue.newInstance();
///
/// void one() {
///     inner();
/// }
///
/// void two() {
///     JDACIntrospection introspection = VAL.get();
///     // the call above will fail
/// }
///
/// void inner() {
///     JDACIntrospection introspection = VAL.get();
///     // use the introspection.instance()
/// }
///
/// void main() {
///     JDACIntrospection introspection = JDACIntrospectionImpl.create(...)
///                                         ...
///                                         .build();
///
///     // calls "one" with the ScopedValue "VAL" set to the introspection variable
///     ScopedValue.where(VAL, introspection).run(() -> one());
///
///     two();
/// }
/// ```
///
/// The value for "VAL" is set for all method calls inside the lambda of `run(() -> one())`. This means
/// that it's accessible inside "one", "inner" and any subsequent method call, but not inside `two` because
/// it is called outside of `run()`.
///
///
/// The [JDACIntrospection] class implements the above logic natively,
/// covering up the underlying [ScopedValue]. Instead of [ScopedValue#get()]
/// you can use [JDACIntrospection#scopedGet(JDACProperty)].
///
/// Please note, that the availability of scoped access to introspection instances vary widely and is different for
/// places inside the callchain. Places where you can use it are marked by [IntrospectionAccess]
/// and additionally include all interaction controller methods. For more information take a look at the wiki.
///
/// ## Properties
/// An instance of [JDACIntrospection] can hold [JDACPropertyProvider]s for all [properties][Property]
/// accessible by its [Scope]. An introspection instance
/// will first compute its own value using the providers registered at it and possibly combine it with the
/// values from the introspection instances' parent(s). For more information on how this is done, visit the documentation of
/// [SingletonPropertySkeleton], [MappingPropertySkeleton] and [EnumerationPropertySkeleton].
///
/// After [computing][JDACPropertyProvider] a value, it will be cached for the lifetime of that introspection instance.
/// Accessing such an instance is threadsafe, and it is guaranteed to always return the same instance.
///
/// ## Listeners
/// A [Listener] registered on an [JDACIntrospection] instance is stored for the lifetime of this
/// instance. It will be called if the event it is registered for, is either fired in this introspection instance itself
/// or any children of it.
///
/// ```java
/// Introspection A = ...;
/// Introspection B with B is children of A
///
/// A.subscribe(FooEvent.class, _ -> System.out.println("Foo fired"));
///
/// publish FooEvent in A -> "Foo fired" printed
/// publish FooEvent in B -> "Foo fired" printed
///
/// // ---------------------------------------
/// B.subsribe(BarEvent.class, _ -> System.out.println("Bar fired"));
///
/// publish BarEvent in A -> nothing printed
/// publish BarEvent in B -> "Bar fired" printed
///
/// ```
///
/// ## Note on Propane
/// [Propane](https://github.com/Goldmensch/propane) is the underlying library, that JDA-Commands is using for its property system.
/// In most cases, you won't directly access propane but the wrappers around it provided by JDA-Commands
/// like [JDACIntrospection], [JDACProperty] etc. These are generated by propane as specializations
/// of general types like [JDACIntrospection] and [Property]. If you debug your bot or encounter any problems, it may be helpful
/// to visit the documentation or [source code]((https://github.com/Goldmensch/propane)) of Propane.
public interface JDACIntrospection extends IntrospectionSkeleton<JDACIntrospection, JDACScope> {

  /// Returns the value for the requested property by either retrieving it from the cache
  /// or computing it according to the [class' documentation][JDACIntrospection].
  ///
  /// @param specific the requested property
  /// @return the value of the requested property
  /// @see JDACIntrospection JDACIntrospection' class documentation
  <T> T get(JDACProperty<T> specific);

  /// Whether scoped access to the introspection instance (by calling [#scopedGet(JDACProperty)])
  /// is possible.
  ///
  /// @return whether scoped access is possible
  static boolean accessible() {
    return JDACIntrospectionImpl.INTROSPECTION.isBound();
  }

  /// Returns the introspection instance set via [ScopedValue] if set, else throws
  /// [NoSuchElementException].
  ///
  /// @return the introspection instance of this scope
  /// @see ScopedValue#get()
  static JDACIntrospection accessScoped() {
    return JDACIntrospectionImpl.INTROSPECTION.get();
  }

  /// Shorthand for `accessScoped().get(property)`. Throws [NoSuchElementException]
  /// if [#accessible()] returns `false`.
  ///
  /// @param property the property to get
  /// @return the value for the property
  static <T> T scopedGet(JDACProperty<T> property) {
    return accessScoped().get(property);
  }

  /// Subscribes to an [event][Event] with the given [Listener].
  /// The provided listener will be stored in this instance, thus be available for the lifetime
  /// of this introspection instance.
  ///
  /// @param listener the [Listener] to be registered
  /// @return a [Subscription] identifying the registered listener
  /// @see JDACIntrospection JDACIntrospection' class documentation
  /// @see JDACEvent
  @Override
  Subscription<JDACIntrospection, JDACScope> subscribe(Listener<? extends Event<JDACScope>, JDACScope, JDACIntrospection> listener);
}
