package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.property.Property;
import dev.goldmensch.propane.property.PropertyProvider;
import io.github.kaktushose.jdac.property.internal.JDACEnumerationProperty;
import io.github.kaktushose.jdac.property.internal.JDACMappingProperty;
import io.github.kaktushose.jdac.property.internal.JDACSingletonProperty;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/// A [JDACPropertyProvider] supplies the values for a [property][JDACProperty].
/// Each [JDACPropertyProvider] is bound to _one_ property for which it can provide values.
///
/// Based on the [Property.Source] of the property, property providers can be registered via different ways. Visit the documentation
/// of [Property.Source] for more information on that matter.
///
/// The possibly [returned][PropertyProvider#supplier()] types vary based on the type of the property:
/// - for [JDACSingletonProperty], a provider can "return" one instance
/// - for [JDACEnumerationProperty], a provider can "return" an instance of [Collection]
/// - for [JDACMappingProperty], a provider can "return" an instance of [Map]
///
/// The values are "provided" by the [supplier][PropertyProvider#supplier()], which is called during resolution.
/// The supplier must be threadsafe and side effect free, as it could be called multiple times during resolution simultaneously.
/// Although that, only one value is stored finally (using a "check-then-act" pattern).
///
/// Furthermore, each [JDACPropertyProvider] has a [priority][PropertyProvider.Priority] stating which providers take
/// precedence over others. For more information on that, visit the documentation of [JDACSingletonProperty], [JDACEnumerationProperty]
/// and [JDACMappingProperty].
///
/// For debugging purpose, [JDACPropertyProvider]s store their "owner", which is the class or "logical unit"
/// (for example the library providing an extension for some property), that can be used to identify an [JDACPropertyProvider]
/// during runtime.
///
/// ## Dependencies on other properties
/// The [supplier][PropertyProvider#supplier()] allows access to the [JDACIntrospection] instance used to resolve this
/// property. This can be used to retrieve the values of other properties needed by this [JDACPropertyProvider], thus creating
/// a dependency on that other property.
///
/// JDA-Commands checks for cycling dependencies during runtime and will provide all needed information to identify the cycle.
///
/// @param <T> the type returned by the supplier
/// @see PropertyProvider
public class JDACPropertyProvider<T> extends PropertyProvider<T, JDACProperty<T>, JDACIntrospection> {
  public JDACPropertyProvider(JDACProperty<T> property, PropertyProvider.Priority priority,
      Class<?> owner, Function<JDACIntrospection, T> supplier) {
    super(property, priority, owner, supplier);
  }
}
