package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Function;

/// # PropertyProviders
/// An [PropertyProvider] provides values for the [Property] associated with it.
///
/// ## Dependencies
/// Because many properties especially services depend on other properties, you can get the values
/// of these properties through the [Context#get(Property)] that is available in the [#supplier()].
///
/// If somehow recursive calls to the same [Property] are created (called cycling dependencies),
/// then a [ConfigurationException] is thrown providing information on how this recursion occurs.
///
/// ## Priorities
/// To decide which value is taken at the end for a property, each [PropertyProvider] defines a priority between
/// 0 and [Integer#MAX_VALUE].
/// The provider with the highest priority will be taken, thus its value is set for the property.
///
/// It's important to note that the priorities 0 to 100 and [Integer#MAX_VALUE] are reserved by JDA-Commands:
/// - priority = 0                      are all fallback/default value provided by JDA-Commands
/// - priority = [Integer#MAX_VALUE]    are all values set by the user manually in [JDACBuilder]
///
/// Usage of reserved priorities will result in an exception causing JDA-Commands to stop itself.
///
/// ## Example
/// An example usage of this class would be:
/// ```java
/// class Foo {
///     public bar(...) {
///         var provider = new PropertyProvider(
///             Type.INTERACTION_CONTROLLER_INSTANTIATOR,
///             2000, // just some random priority
///             Foo.class, // used as a waypoint for debugging
///             ctx -> new MyInteractionControllerInstantiator(ctx.get(Property.I18N)) // needs the I18n instance
///         )
///     }
/// }
/// ```
///
/// @param type the [Property] for which this [PropertyProvider] creates values.
/// @param priority the priority of this provider
/// @param referenceClass   The Class to which this [PropertyProvider] 'belongs'. It's only used for logging purposes.
///                         For example, all fallback/default values have [JDACBuilder] as their reference class.
/// @param supplier the [Function] returning the properties value, provides access to [Context]
public record PropertyProvider<T>(
        Property<T> type,
        int priority,
        Class<?> referenceClass,
        Function<Context, @Nullable T> supplier
) implements Comparable<PropertyProvider<T>> {

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /// Creates a new [PropertyProvider] where its [#referenceClass()] is the caller of this method.
    ///
    /// @param type the [Property] for which this [PropertyProvider] creates values
    /// @param priority the priority of this provider
    /// @param supplier the [Function] returning the properties value, provides access to [Context]
    /// @return the [PropertyProvider] instance
    ///
    /// @see StackWalker#getCallerClass()
    public static <T> PropertyProvider<T> create(Property<T> type, int priority, Function<Context, @Nullable T> supplier) {
        Class<?> referenceClass = WALKER.getCallerClass();
        return new PropertyProvider<>(type, priority, referenceClass, supplier);
    }

    /// Compares two [PropertyProvider] by their [#priority()] in reverse order.
    /// The [PropertyProvider] with the higher priority comes first.
    ///
    /// @return {@inheritDoc}
    @Override
    public int compareTo(PropertyProvider<T> o) {
        return Comparator.<Integer>reverseOrder().compare(priority, o.priority);
    }

    /// This context provides useful functionality to a [PropertyProvider] during value creation.
    public interface Context {

        /// This method will resolve the value of a [Property].
        ///
        /// @return the value associated with the given [Property]
        ///
        /// @throws ConfigurationException may be thrown if cycling dependencies are detected
        ///
        /// @implNote The values are resolved lazily and cached. Each value will only be resolved once.
        <T> T get(Property<T> type);
    }
}
