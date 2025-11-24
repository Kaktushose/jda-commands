package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDACBuilder;

import java.util.Collection;
import java.util.ServiceLoader.Provider;
import java.util.function.Predicate;

/// A [Predicate] to test whether an [Extension] should be loaded or not.
@SuppressWarnings("rawtypes") // because of stream in ExtensionLoader must handle raw type Provider<Extension>
public record ExtensionFilter(JDACBuilder.FilterStrategy filterStrategy, Collection<String> classes)
        implements Predicate<Provider<Extension>> {

    @Override
    public boolean test(Provider<Extension> provider) {
        Predicate<String> startsWith = s -> provider.type().getName().startsWith(s);
        return switch (filterStrategy) {
            case INCLUDE -> classes.stream().anyMatch(startsWith);
            case EXCLUDE -> classes.stream().noneMatch(startsWith);
        };
    }
}
