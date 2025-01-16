package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.guice.internal.GuiceInstanceProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jetbrains.annotations.NotNull;

/// The implementation of [Extension] for using Google's [Guice] as an [InstanceProvider].
///
/// @see GuiceExtensionData
public class GuiceExtension implements Extension<GuiceExtensionData> {

    @Override
    public void configure(@NotNull JDACommandsBuilder builder, GuiceExtensionData data) {
        Injector injector = data != null
                ? data.providedInjector()
                : Guice.createInjector();

        builder.instanceProvider(new GuiceInstanceProvider(injector, false));
    }

    @Override
    public @NotNull Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }
}
