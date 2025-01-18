package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.ReadOnlyJDACommandsBuilder;
import com.github.kaktushose.jda.commands.guice.internal.GuiceRootInstanceProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// The implementation of [Extension] for using Google's [Guice] as an [InstanceProvider].
///
/// @see GuiceExtensionData
public class GuiceExtension implements Extension {

    private JDACommands jdaCommands;
    private Injector injector;
    private GuiceRootInstanceProvider provider;

    @Override
    public void init(ReadOnlyJDACommandsBuilder builder, @Nullable Data data) {
        this.injector = data != null
                ? ((GuiceExtensionData) data).providedInjector()
                : Guice.createInjector();
        this.provider = new GuiceRootInstanceProvider(this);
    }

    @Override
    public Collection<Object> providedImplementations() {
        return List.of(provider);
    }

    @Override
    public void afterFrameworkInit(JDACommands jdaCommands) {
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
