package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.extension.Implementation.ExtensionImplementable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// Implementations of this interface, that are registered by Javas service provider interface, will be called
/// in [JDACBuilder] to configure the framework.
///
/// This class provides ways to extend the framework with own functionality:
/// - [#providedImplementations()]: By implementing this method and returning a list of own implementations of
/// interfaces marked with [ExtensionImplementable], you can for example provide an own implementation of [InteractionControllerInstantiator]
/// or [Descriptor]. These implementations will override the default ones.
///
/// If the implementation of this class needs additional configuration data, implementations have to provide an
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
///     public @NotNull Collection<Implementation<?>> providedImplementations() {
///         return List.of(new Implementation<>(
///                 InteractionClassProvider.class,
///                 _ -> new CustomInteractionClassProvider(this))
///         );
///     }
///
///     @Override
///     public @NotNull Class<GuiceExtensionData> dataType() {
///         return DIExtensionData.class;
///     }
/// }
///
/// public record DIExtensionData(Injector providedInjector) implements Extension.Data {}
/// ```
public interface Extension {

    /// Will be called right after jda-commands loaded the Extension.
    ///
    /// @param data The custom implementation of [Data] if given by the User. This can be safely cast to the type returned by [#dataType()].
    void init(@Nullable Data data);

    /// By implementing this method and returning a list of own implementations of interfaces marked with
    /// [ExtensionImplementable], you can for example provide an own implementation of [InteractionClassProvider]
    /// or [Descriptor]. These implementations will override the default ones.
    ///
    /// @return a collection of [Implementation]s that should be used to retrieve certain implementations of an interface.
    /// @apiNote Please note that this method is called multiple times during framework creation. If the identity of the implementations
    /// is important, you should always return the same instance.
    @NotNull
    default Collection<@NotNull Implementation<?>> providedImplementations() {
        return List.of();
    }

    /// @return the [Class] of the custom [Data] implementation
    @NotNull
    default Class<?> dataType() {
        return Void.class;
    }

    /// Implementations of this interface are providing additional configuration to implementations of [Extension]
    interface Data {}

}
