package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.property.Priority;
import dev.goldmensch.propane.property.Property;
import dev.goldmensch.propane.property.PropertyProviderSkeleton;
import io.github.kaktushose.jdac.JDACBuilder;
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
/// The possibly [returned][JDACPropertyProvider#supplier()] types vary based on the type of the property:
/// - for [`JDACSingletonProperty`][dev.goldmensch.propane.property.SingletonPropertySkeleton], a provider can "return" one instance
/// - for [`JDACEnumerationProperty`][dev.goldmensch.propane.property.EnumerationPropertySkeleton], a provider can "return" an instance of [Collection]
/// - for [`JDACMappingProperty`][dev.goldmensch.propane.property.MappingPropertySkeleton], a provider can "return" an instance of [Map]
///
/// The values are "provided" by the [supplier][JDACPropertyProvider#supplier()], which is called during resolution.
/// The supplier must be threadsafe and side effect free, as it could be called multiple times during resolution simultaneously.
/// Although that, only one value is stored finally (using a "check-then-act" pattern).
///
/// Furthermore, each [JDACPropertyProvider] has a [priority][Priority] stating which providers take
/// precedence over others. For more information on that, visit the documentation of [`JDACSingletonProperty`][dev.goldmensch.propane.property.SingletonPropertySkeleton], [`JDACEnumerationProperty`][dev.goldmensch.propane.property.EnumerationPropertySkeleton]
/// and [`JDACMappingProperty`][dev.goldmensch.propane.property.MappingPropertySkeleton].
///
/// Following priorities have a special meaning in JDA-Commands:
/// - priority = [Priority#FALLBACK]    are all fallback/default values provided by JDA-Commands
/// - priority = [Priority#BUILDER]     are all values set by the user manually in [JDACBuilder]
///-  priority between 1 and 100        are reserved for internal usage.
///
/// Usage of reserved priorities will result in an exception causing JDA-Commands to stop itself.
///
/// For debugging purpose, [JDACPropertyProvider]s store their "owner", which is the class or "logical unit"
/// (for example the library providing an extension for some property), that can be used to identify an [JDACPropertyProvider]
/// during runtime.
///
/// ## Dependencies on other properties
/// The [supplier][JDACPropertyProvider#supplier()] allows access to the [JDACIntrospection] instance used to resolve this
/// property. This can be used to retrieve the values of other properties needed by this [JDACPropertyProvider], thus creating
/// a dependency on that other property.
///
/// JDA-Commands checks for cycling dependencies during runtime and will provide all needed information to identify the cycle.
///
/// @param <T> the type returned by the supplier
/// @see JDACPropertyProvider
public class JDACPropertyProvider<T> extends PropertyProviderSkeleton<T, JDACProperty<T>, JDACIntrospection> {
  public JDACPropertyProvider(JDACProperty<T> property, Priority priority,
      Class<?> owner, Function<JDACIntrospection, T> supplier) {
    super(property, priority, owner, supplier);
  }
}
