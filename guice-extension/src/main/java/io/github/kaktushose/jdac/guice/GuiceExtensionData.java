package io.github.kaktushose.jdac.guice;

import io.github.kaktushose.jdac.extension.Extension;
import com.google.inject.Injector;

/// Custom [Extension.Data] to be used to configure this extension.
///
/// @param providedInjector The [Injector] to be used instead of creating one
public record GuiceExtensionData(
        Injector providedInjector
) implements Extension.Data {
}
