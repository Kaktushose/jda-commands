package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.guice.internal.GuiceRootInstanceProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jetbrains.annotations.NotNull;

/// The implementation of [Extension] for using Google's [Guice] as an [InstanceProvider].
///
/// @see GuiceExtensionData
public class GuiceExtension implements Extension<GuiceExtensionData> {

    private JDACommands jdaCommands;
    private Injector injector;

    @Override
    public void configure(@NotNull JDACommandsBuilder builder, GuiceExtensionData data) {
         this.injector = data != null
                ? data.providedInjector()
                : Guice.createInjector();

        builder.instanceProvider(new GuiceRootInstanceProvider(this));
    }

    @Override
    public void afterInit(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
    }

    @Override
    public @NotNull Class<GuiceExtensionData> dataType() {
        return GuiceExtensionData.class;
    }

    public Injector injector() {
        return injector;
    }

    public JDACommands jdaCommands() {
        return jdaCommands;
    }
}
