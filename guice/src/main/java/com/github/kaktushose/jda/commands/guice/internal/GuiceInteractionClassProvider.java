package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionClassProvider;
import com.github.kaktushose.jda.commands.guice.GuiceExtension;
import com.google.inject.Injector;

public class GuiceInteractionClassProvider implements InteractionClassProvider {

    private final GuiceExtension extension;

    public GuiceInteractionClassProvider(GuiceExtension extension) {
        this.extension = extension;
    }

    @Override
    public <T> T instance(Class<T> clazz, Context context) {
        throw new UnsupportedOperationException("GuiceInteractionClassProvider must be used runtime bound!");
    }

    /// Creates a new child injector with its own [JDACommandsModule] for each runtime.
    /// This has the effect, that each class annotated with [Interaction] will be treated as a runtime scoped singleton.
    @Override
    public InteractionClassProvider forRuntime(String id) {
        Injector childInjector = extension.injector().createChildInjector(new JDACommandsModule(extension.jdaCommands()));

        return new InteractionClassProvider() {
            @Override
            public <T> T instance(Class<T> clazz, Context context) {
                return childInjector.getInstance(clazz);
            }
        };
    }
}
