package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.dispatching.instantiation.spi.InstantiatorProvider;
import com.google.inject.Injector;
import org.jetbrains.annotations.NotNull;

public record GuiceInstantiatorData(
        @NotNull
        Injector providedInjector
) implements InstantiatorProvider.Data {
}
