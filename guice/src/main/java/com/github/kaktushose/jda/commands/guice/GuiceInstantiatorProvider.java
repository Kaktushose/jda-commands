package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.dispatching.instantiation.Instantiator;
import com.github.kaktushose.jda.commands.dispatching.instantiation.spi.InstantiatorProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceInstantiatorProvider implements InstantiatorProvider {
    @Override
    public Instantiator create() {
        Injector injector = Guice.createInjector();

        return new GuiceInstantiator(false, injector);
    }

    @Override
    public int priority() {
        return 0;
    }
}
