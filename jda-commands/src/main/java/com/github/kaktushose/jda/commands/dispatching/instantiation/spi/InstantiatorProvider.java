package com.github.kaktushose.jda.commands.dispatching.instantiation.spi;

import com.github.kaktushose.jda.commands.dispatching.instantiation.Instantiator;

public interface InstantiatorProvider {
    Instantiator create(Data data);
    int priority();

    interface Data {}
}
