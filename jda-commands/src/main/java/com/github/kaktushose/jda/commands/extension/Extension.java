package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionClassProvider;
import com.github.kaktushose.jda.commands.extension.Implementation.ExtensionImplementable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// Implementations of this interface, that are registered by java's service provider interface, will be called
/// in [com.github.kaktushose.jda.commands.JDACommandsBuilder] to configure the framework.
///
/// This class provides ways to extend the framework with own functionality:
/// - [#providedImplementations()]: By implementing this method and returning a list of own implementations of
/// interfaces marked with [ExtensionImplementable], you can for example provide an own implementation of [InteractionClassProvider]
/// or [Descriptor]. These implementations will override the default ones.
///
/// If the implementation of this class needs additional configuration data, implementations have to provide an
/// own implementation of [Data] that the user have to register in the builder before the extensions are loaded.
///
/// ### Example
/// This examples extension provides an own implementation of [InteractionClassProvider]
/// ```java
/// public class DIExtension implements Extension {
///
///    private Injector injector;
///
///    @Override
///    public void init(@Nullable Data data) {
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
///    }
///
///     @Override
///     public void afterFrameworkInit(@NotNull JDACommands jdaCommands) {
///         this.injector.addBean(jdaCommands);
///    }
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

    /// Will be called after retrieving and creation an instance of the implementation.
    /// @param data The custom implementation of [Data] if given by the User. This can be safely cast to the type returned by [#dataType()].
    void init(@Nullable Data data);

    /// By implementing this method and returning a list of own implementations of interfaces marked with
    /// [ExtensionImplementable], you can for example provide an own implementation of [InteractionClassProvider]
    /// or [Descriptor]. These implementations will override the default ones.
    ///
    /// @apiNote Please note that this method is called multiple times during framework creation. If the identity of the implementations
    /// is important, you should always return the same instance.
    ///
    /// @return a collection of [Implementation]s that should be used to retrieve certain implementations of an interface.
    @NotNull
    default Collection<Implementation<?>> providedImplementations() {
        return List.of();
    }


    /// Called after creation of the [JDACommands] object but before starting the framework.
    ///
    /// Can be used to store/inject the JDACommands instance before the user can use any part of the framework. Useful
    /// for own implementations of [InteractionClassProvider] for example.
    ///
    /// @param jdaCommands the instantiated [JDACommands] instance
    default void afterFrameworkInit(@NotNull JDACommands jdaCommands) {}

    /// Called after starting the framework.
    ///
    /// @param jdaCommands the instantiated and started [JDACommands]
    default void afterFrameworkStart(@NotNull JDACommands jdaCommands) {}

    /// @return the [Class] of the custom [Data] implementation
    @NotNull
    default Class<?> dataType() {
        return Void.class;
    }

    /// Implementations of this interface are providing additional configuration to implementations of [Extension]
    interface Data {}

}
