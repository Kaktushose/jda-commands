package io.github.kaktushose.jdac.extension.internal;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.extension.Extension;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.ServiceLoader.Provider;
import java.util.function.Predicate;

/// A [Predicate] to test whether an [Extension] should be loaded or not.
@ApiStatus.Internal
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
