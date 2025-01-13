package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.dispatching.instantiation.InstantiationContext;
import com.github.kaktushose.jda.commands.dispatching.instantiation.Instantiator;
import com.google.inject.Injector;

public class GuiceInstantiator implements Instantiator {

    private final boolean runtimeBound;
    private final Injector injector;

    public GuiceInstantiator(boolean runtimeBound, Injector injector) {
        this.runtimeBound = runtimeBound;
        this.injector = injector;
    }

    @Override
    public <T> T instantiate(Class<T> clazz, InstantiationContext context) {
        if (!runtimeBound) {
            throw new UnsupportedOperationException("Guice Instantiators must be runtime bound!");
        }

        return injector.getInstance(clazz);
    }

    @Override
    public Instantiator forRuntime(String id) {
        Injector runtimeBoundInjector = injector.createChildInjector(new JDACommandsModule());
        return new GuiceInstantiator(true, runtimeBoundInjector);
    }
}
