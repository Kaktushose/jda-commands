package io.github.kaktushose.jdac.message.i18n;

import dev.goldmensch.fluava.Fluava;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
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
public interface Localizer {

    /// This method localizes a given key for a specific bundle and locale.
    /// If no message is found for this combination of locale, bundle and key or an error occurred
    /// [Optional#empty()] should be returned.
    ///
    /// @param locale    the asked for locale
    /// @param bundle    the bundle to search the key in
    /// @param key       the key of the message as provided by the user
    /// @param arguments values for placeholder in the message
    /// @return the localized message or [Optional#empty()] if no key is found/an error occurred
    Optional<String> localize(Locale locale, String bundle, String key, Map<String, @Nullable Object> arguments);

    /// This method localizes a given key (coming from JDA's [LocalizationFunction]) for a specific bundle and locale.
    /// If no message is found for this combination of locale, bundle and key or an error occurred
    /// [Optional#empty()] should be returned.
    ///
    /// The difference to [#localize(Locale, String, String, Map)] is, that this method is only
    /// used inside our implementation of [LocalizationFunction], thus the keys always follow the JDA format (parts
    /// are separated by a dot `.`).
    ///
    /// For example, project fluent ([Fluava]) disallows `.` as a "normal" separator in localization keys, therefore
    /// we have to replace all `.` with `-` and then pass it to the [#localize(Locale, String, String, Map)] method.
    ///
    /// @param locale    the asked for locale
    /// @param bundle    the bundle to search the key in
    /// @param key       the key of the message as provided by JDA
    /// @param arguments values for placeholder in the message
    /// @return the localized message or [Optional#empty()] if no key is found/an error occurred
    default Optional<String> localizeJDA(
            Locale locale,
            String bundle,
            String key,
            Map<String, @Nullable Object> arguments
    ) {
        return localize(locale, bundle, key, arguments);
    }
}
