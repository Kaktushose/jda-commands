package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// Implementations of this interface, that are registered by Javas service provider interface, will be called
/// in [JDACBuilder] to configure the framework.
///
/// This interface provides ways to extend the framework with own functionality:
/// - [#providedImplementations()]: By implementing this method and returning a collection of [Implementation]s, you can
/// for example provide an own implementation of [InteractionControllerInstantiator] or [Descriptor]. These
/// implementations will override the default ones.
///
/// - If the [Extension] needs additional configuration data, implementations have to provide an
/// own implementation of [Data] that the user has to register in the builder by calling [JDACBuilder#extensionData(Data...)].
///
/// ### Example
/// This example extension provides an own implementation of [InteractionControllerInstantiator].
/// ```java
/// public class DIExtension implements Extension {
///
///     private Injector injector;
///
///     @Override
///     public void init(@Nullable Data data) {
///         this.injector = data != null
///                 ? ((DIExtensionData) data).providedInjector()
///                 : DI.createInjector();
///     }
///
///     @Override
///     public Collection<Implementation<?>> providedImplementations() {
///         return List.of(new Implementation<>(
///                 InteractionClassProvider.class,
///                 _ -> new CustomInteractionClassProvider(this))
///         );
///     }
///
///     @Override
///     public Class<GuiceExtensionData> dataType() {
///         return DIExtensionData.class;
///     }
/// }
///
/// public record DIExtensionData(Injector providedInjector) implements Extension.Data {}
/// ```
public interface Extension<T extends Extension.Data> {

    /// Initialises the [Extension] with the provided [Data]. Will be called right after jda-commands loaded the Extension.
    ///
    /// @param data The custom implementation of [Data] if given by the User. This can be safely cast to the type returned by [#dataType()].
    void init(@Nullable T data);

    /// Gets a collection of [Implementation]s this [Extension] provides.
    ///
    /// @return a collection of [Implementation]s
    /// @implNote Please note that this method is called multiple times during framework creation. If the identity of the implementations
    /// is important, you should always return the same instance.
    default Collection<Implementation<?>> providedImplementations() {
        return List.of();
    }

    /// @return the [Class] of the custom [Data] implementation or null if the extension doesn't support additional configuration
    @Nullable
    default Class<T> dataType() {
        return null;
    }

    /// Implementations of this interface are providing additional configuration to implementations of [Extension].
    interface Data {}

}
