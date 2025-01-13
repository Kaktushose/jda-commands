package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.dispatching.instantiation.Instantiator;
import com.github.kaktushose.jda.commands.dispatching.instantiation.spi.InstantiatorProvider;
import com.github.kaktushose.jda.commands.guice.GuiceInstantiatorData;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceInstantiatorProvider implements InstantiatorProvider {

    @Override
    public Instantiator create(InstantiatorProvider.Data data) {

        var injector = switch (data) {
            case GuiceInstantiatorData(Injector provided) -> provided;
            case null, default -> Guice.createInjector();
        };

        return new GuiceInstantiator(false, injector);
    }

    @Override
    public int priority() {
        return 0;
    }
}
