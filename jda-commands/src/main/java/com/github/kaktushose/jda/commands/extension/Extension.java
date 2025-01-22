package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// Implementations of this interface, that are registered by java's service provider interface, will be called
/// in [JDACommandsBuilder] to configure the framework.
///
/// Extensions can for example set an implementation of [InstanceProvider] or [Descriptor] or
/// register additional implementations of [TypeAdapter].
///
/// If the implementation of this class needs additional configuration, implementations have to provide an
/// own implementation of [Data] that the user have to register in the builder before the extensions are loaded.
public interface Extension {

    /// Will be called after retrieving and creation an instance of the implementation.
    /// @param data The custom implementation of [Data] if given by the User. This can be safely cast to the type returned by [#dataType()].
    void init(@Nullable Data data);

    /// Please note that this method is called multiple times during framework creation. If the identity of the implementations
    /// is important, you should always return the same instance.
    ///
    /// @return a collection of [ImplementationProvider]s that should be used to retrieve certain implementations of an interface.
    @NotNull
    default Collection<ImplementationProvider<?>> providedImplementations() {
        return List.of();
    }


    /// Called after creation of the [JDACommands] object but before starting the framework.
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
