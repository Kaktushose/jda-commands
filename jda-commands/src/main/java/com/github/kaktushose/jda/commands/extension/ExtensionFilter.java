package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public class ExtensionFilter implements Predicate<ServiceLoader.Provider<Extension>> {

    private final Collection<String> classes;
    private final JDACommandsBuilder.FilterStrategy filterStrategy;

    public ExtensionFilter(JDACommandsBuilder.FilterStrategy filterStrategy, Collection<String> classes) {
        this.classes = classes;
        this.filterStrategy = filterStrategy;
    }

    @Override
    public boolean test(ServiceLoader.Provider<Extension> provider) {
        Predicate<String> startsWith = s -> provider.type().getName().startsWith(s);
        return switch (filterStrategy) {
            case INCLUDE -> classes.stream().anyMatch(startsWith);
            case EXCLUDE -> classes.stream().noneMatch(startsWith);
        };
    }
}
