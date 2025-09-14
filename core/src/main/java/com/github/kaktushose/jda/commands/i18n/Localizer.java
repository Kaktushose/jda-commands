package com.github.kaktushose.jda.commands.i18n;

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

    /// This method localizes the given message content for the specific locale and arguments.
    /// The purpose of this method is to support replacing placeholders in text fields of components, embeds
    /// and modals that aren't retrieved from [#localize(Locale, String, String, Map)]
    ///
    /// @param locale    the asked for locale
    /// @param arguments values for placeholder in the message
    /// @param content   the content of the message to be localized, the content should be treated as if it's received from some localization file
    /// @return the localized message or [Optional#empty()] if an error occurred
    Optional<String> localizeMessage(Locale locale, String content, Map<String, @Nullable Object> arguments);
}
