package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.property.PropertyProvider;
import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;

import java.lang.Class;
import java.util.function.Function;

/// # PropertyProviders
/// An [io.github.kaktushose.jdac.configuration.PropertyProvider] provides values for the [Property] associated with it.
///
/// ## Dependencies
/// Because many properties especially services depend on other properties, you can get the values
/// of these properties through the [io.github.kaktushose.jdac.configuration.PropertyProvider.Context#get(Property)] that is available in the [#supplier()].
///
/// If somehow recursive calls to the same [Property] are created (called cycling dependencies),
/// then a [ConfigurationException] is thrown providing information on how this recursion occurs.
///
/// ## Priorities
/// To decide which value is taken at the end for a property, each [io.github.kaktushose.jdac.configuration.PropertyProvider] defines a priority between
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
///             Property.INSTANTIATOR,
///             2000, // just some random priority
///             Foo.class, // used as a waypoint for debugging
///             ctx -> new MyInstantiator(ctx.get(Property.I18N)) // needs the I18n instance
///         )
///     }
/// }
/// ```
///
/// @param type the [Property] for which this [io.github.kaktushose.jdac.configuration.PropertyProvider] creates values.
/// @param priority the priority of this provider
/// @param referenceClass   The Class to which this [io.github.kaktushose.jdac.configuration.PropertyProvider] 'belongs'. It's only used for logging purposes.
///                         For example, all fallback/default values have [JDACBuilder] as their reference class.
/// @param supplier the [Function] returning the properties value, provides access to [io.github.kaktushose.jdac.configuration.PropertyProvider.Context]
public class JDACPropertyProvider<T> extends PropertyProvider<T, JDACProperty<T>, JDACIntrospection> {
  public JDACPropertyProvider(JDACProperty<T> property, PropertyProvider.Priority priority,
      Class<?> owner, Function<JDACIntrospection, T> supplier) {
    super(property, priority, owner, supplier);
  }
}
