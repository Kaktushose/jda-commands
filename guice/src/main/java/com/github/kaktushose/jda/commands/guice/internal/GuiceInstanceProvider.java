package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.google.inject.Injector;

public class GuiceInstanceProvider implements InstanceProvider {

    private final Injector injector;
    private final boolean runtimeBound;

    public GuiceInstanceProvider(Injector injector, boolean runtimeBound) {
        this.injector = injector;
        this.runtimeBound = runtimeBound;
    }

    @Override
    public <T> T instance(Class<T> clazz, Context context) {
        if (!runtimeBound) {
            throw new UnsupportedOperationException("GuiceInstanceProvider must be used runtime bound!");
        }

        return injector.getInstance(clazz);
    }

    /// Creates a new child injector with its own [JDACommandsModule] for each runtime.
    /// This has the effect, that each class annotated with [Interaction] will be treated as a runtime scoped singleton.
    @Override
    public InstanceProvider forRuntime(String id) {
        return new GuiceInstanceProvider(injector.createChildInjector(new JDACommandsModule()), true);
    }
}
