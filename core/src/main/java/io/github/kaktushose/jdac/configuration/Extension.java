package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.configuration.Property.Category;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// # Extensions
/// Implementations of this interface, that are registered by Javas service provider interface, will be called
/// in [JDACBuilder] to configure the framework.
///
/// This interface provides ways to extend the framework with own functionality:
///
///
/// ## Properties
/// By implementing [#properties()] and returning a collection of [PropertyProvider]s, you can
/// for example provide an own implementation of [InteractionControllerInstantiator]
/// or [Descriptor]. These implementations will override the default ones.
///
/// It's important to know, that you can only return [PropertyProvider]s of [Property]s
/// with [Property#category()] set to [Category#LOADABLE]!
///
/// ## Extension Configuration ([Extension.Data])
/// If the [Extension] needs additional configuration data, implementations have to provide an
/// own implementation of [Data] that the user has to register in the builder by calling
///  [JDACBuilder#extensionData(Data...)].
///
/// ## On Framework Start
/// At the time that framework is fully initialized and started (practically at the end of [JDACBuilder#start()]),
/// the [Extension#onStart(JDACommands)] method of all extensions will be called.
/// This allows further configuration, e.g. through the [Introspection] API by using [JDACommands#introspection()].
///
/// ## Example
/// This example extension provides an own implementation of [InteractionControllerInstantiator].
/// ```java
/// public class DIExtension implements Extension<DIExtensionData> {
///
///     private Injector injector;
///
///     @Override
///     public void init(@Nullable DIExtensionData data) {
///         this.injector = data != null
///                 ? data.providedInjector()
///                 : DI.createInjector();
///     }
///
///     @Override
///     public Collection<PropertyProvider<?>> properties() {
///         return List.of(PropertyProvider.create(
///                 Property.INTERACTION_CONTROLLER_INSTANTIATOR,
///                 2000,
///                 _ -> new CustomInteractionClassProvider(this))
///         );
///     }
///
///     @Override
///     public void onStart(JDACommands framework) {
///         Introspection introspection = framework.introspection();
///
///         introspection.subscribe(RuntimeCloseEvent.class, (event, _) -> ...);
///     }
///
///     @Override
///     public Class<DIExtensionData> dataType() {
///         return DIExtensionData.class;
///     }
/// }
///
/// public record DIExtensionData(Injector providedInjector) implements Extension.Data {}
/// ```
public interface Extension<T extends Extension.Data> {

    /// Initialises the [Extension] with the provided [Data]. Will be called right after jda-commands loaded the
    /// Extension.
    ///
    /// @param data The custom implementation of [Data] if given by the User. This can be safely cast to the type
    /// returned by [#dataType()].
    void init(@Nullable T data);

    /// Gets a collection of [PropertyProvider]s this [Extension] provides.
    ///
    /// The [Property]s provided by these providers must have [Property#category()] set to [Category#LOADABLE]!
    ///
    /// @return a collection of [PropertyProvider]s
    default Collection<PropertyProvider<?>> properties() {
        return List.of();
    }

    /// This method will be called after the framework was fully started, practically at the end of
    ///  [JDACBuilder#start()].
    ///
    /// @param framework the fully initialized [JDACommands] instance.
    @IntrospectionAccess(Stage.INITIALIZED)
    default void onStart(JDACommands framework) {
    }

    /// @return the [Class] of the custom [Data] implementation or null if the extension doesn't support additional
    /// configuration
    @Nullable
    default Class<T> dataType() {
        return null;
    }

    /// Implementations of this interface are providing additional configuration to implementations of [Extension].
    interface Data { }

}
