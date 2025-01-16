package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Implementations of this interface, that are registered by java's service provider interface, will be called
/// in [JDACommandsBuilder] to configure the framework.
///
/// Extensions can for example set an implementation of [InstanceProvider] or [Descriptor] or
/// register additional implementations of [TypeAdapter].
///
/// If the implementation of this class needs additional configuration, implementations have to provide an
/// own implementation of [Data] that the user have to register in the builder before the extensions are loaded.
public interface Extension<T extends Extension.Data> {
    /// Will be called by the [JDACommandsBuilder] at a specific time to let the specific implementation
    /// configure the framework.
    ///
    /// Please note that extensions shouldn't override user specific options and are mainly
    /// intended to register things like [InstanceProvider], [Descriptor] or additional [TypeAdapter]s
    ///
    /// For further information please take a look at [JDACommandsBuilder].
    ///
    /// @param builder the used instance of [JDACommandsBuilder]
    /// @see JDACommandsBuilder#applyExtensions(JDACommandsBuilder.FilterStrategy, String...)
    void configure(@NotNull JDACommandsBuilder builder, @Nullable T data);

    /// @return the [Class] of the custom [Data] implementation
    @NotNull
    default Class<T> dataType() {
        return null;
    }

    /// Implementations of this interface are providing additional configuration to implementations of [Extension]
    interface Data {}

}
