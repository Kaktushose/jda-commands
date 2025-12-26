package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDACBuilder;

import java.util.Collection;
import java.util.ServiceLoader.Provider;
import java.util.function.Predicate;

/// A [Predicate] to test whether an [Extension] should be loaded or not.
///
/// @param filterStrategy the [FilterStrategy] to use
/// @param classes the names of base packages of the [Class]es to filter according to the used [FilterStrategy]
///
/// @see JDACBuilder#filterExtensions(FilterStrategy, String...)
/// @see #test(Provider)
@SuppressWarnings("rawtypes") // because of stream in Extensions must handle raw type Provider<Extension>
public record ExtensionFilter(FilterStrategy filterStrategy, Collection<String> classes)
        implements Predicate<Provider<Extension>> {

    /// Tests whether an [Extension] should be used or not according to this filter.
    /// The
    ///
    /// @param provider the 'unloaded' [Extension] to test (still wrapped by [Provider])
    ///
    /// @return whether to use the extension
    /// @implNote   This method compares the [`fully classified class name`][Class#getName()]
    ///             of the [Extension] implementation to [#classes()] by using [String#startsWith(String)],
    ///             so it's possible to include/exclude an extensions by
    ///             only using a part of the full name/packages.
    @Override
    public boolean test(Provider<Extension> provider) {
        Predicate<String> startsWith = s -> provider.type().getName().startsWith(s);
        return switch (filterStrategy) {
            case INCLUDE -> classes.stream().anyMatch(startsWith);
            case EXCLUDE -> classes.stream().noneMatch(startsWith);
        };
    }

    /// The two available filter strategies
    public enum FilterStrategy {
        /// includes the defined classes
        INCLUDE,
        /// excludes the defined classes
        EXCLUDE
    }
}
