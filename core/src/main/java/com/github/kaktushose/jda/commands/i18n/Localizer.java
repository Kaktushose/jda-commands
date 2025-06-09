package com.github.kaktushose.jda.commands.i18n;

import com.github.kaktushose.jda.commands.extension.Implementation;
import dev.goldmensch.fluava.Fluava;

import java.util.Locale;
import java.util.Map;

/// A [Localizer] is used to localize a given key for a specific bundle and locale.
///
/// Implementations of this interface will commonly delegate the task to a localization framework like
/// [Fluava].
public non-sealed interface Localizer extends Implementation.ExtensionProvidable {

    /// This method localizes a given key for a specific bundle and locale.
    /// If no message is found for this combination of locale, bundle and key, the key
    /// should be returned.
    ///
    /// @param locale the asked for locale
    /// @param bundle the bundle to search the key in
    /// @param key the key of the message
    /// @param arguments values for placeholder in the message
    /// @return the localized message or the key if no localization is found
    String localize(Locale locale, String bundle, String key, Map<String, Object> arguments);
}
