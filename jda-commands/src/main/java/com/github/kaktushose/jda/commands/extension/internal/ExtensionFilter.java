package com.github.kaktushose.jda.commands.extension.internal;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.extension.Extension;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public record ExtensionFilter(
        JDACommandsBuilder.FilterStrategy filterStrategy,
        Collection<String> classes
) implements Predicate<ServiceLoader.Provider<Extension>> {

    @Override
    public boolean test(ServiceLoader.Provider<Extension> provider) {
        Predicate<String> startsWith = s -> provider.type().getName().startsWith(s);
        return switch (filterStrategy) {
            case INCLUDE -> classes.stream().anyMatch(startsWith);
            case EXCLUDE -> classes.stream().noneMatch(startsWith);
        };
    }
}
