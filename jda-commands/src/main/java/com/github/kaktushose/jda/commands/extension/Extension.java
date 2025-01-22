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

    void init(@Nullable Data data);

    default Collection<ImplementationProvider<?>> providedImplementations() {
        return List.of();
    }

    default void afterFrameworkInit(JDACommands jdaCommands) {}

    default void afterFrameworkStart(JDACommands jdaCommands) {}

    /// @return the [Class] of the custom [Data] implementation
    @NotNull
    default Class<?> dataType() {
        return null;
    }

    /// Implementations of this interface are providing additional configuration to implementations of [Extension]
    interface Data {}

}
