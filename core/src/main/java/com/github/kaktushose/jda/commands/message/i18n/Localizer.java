package com.github.kaktushose.jda.commands.message.i18n;

import com.github.kaktushose.jda.commands.extension.Implementation;
import dev.goldmensch.fluava.Fluava;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/// A [Localizer] is used to localize a given key for a specific bundle and locale.
///
/// Implementations of this interface will commonly delegate the task to a localization framework like
/// [Fluava].
///
/// For an example implementation take a look at [FluavaLocalizer]
public non-sealed interface Localizer extends Implementation.ExtensionProvidable {

    /// This method localizes a given key for a specific bundle and locale.
    /// If no message is found for this combination of locale, bundle and key or an error occurred
    /// [Optional#empty()] should be returned.
    ///
    /// @param locale    the asked for locale
    /// @param bundle    the bundle to search the key in
    /// @param key       the key of the message
    /// @param arguments values for placeholder in the message
    /// @return the localized message or [Optional#empty()] if no key is found/an error occurred
    Optional<String> localize(Locale locale, String bundle, String key, Map<String, @Nullable Object> arguments);
}
