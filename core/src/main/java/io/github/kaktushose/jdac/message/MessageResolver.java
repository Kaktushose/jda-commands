package io.github.kaktushose.jdac.message;

import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/// The MessageResolver combines [I18n#localize(Locale, String, Entry...)] and
/// [EmojiResolver#resolve(String)].
///
/// It will resolve the message in following order:
///
/// 1. resolve placeholders with help of [PlaceholderResolver]
/// 2. do localization with help of [I18n]
/// 3. resolve emojis with help [EmojiResolver]
///
///
/// Please note that this class is a helper and doesn't have own resolving logic, it's more of a pipeline
/// to [EmojiResolver] and [I18n]. It is not intended to be directly used by end users but part of the public api
/// to allow manual execution of the frameworks resolving logic for dynamic values if needed.
public class MessageResolver {
    private final I18n i18n;
    private final EmojiResolver emojiResolver;

    public MessageResolver(I18n i18n, EmojiResolver emojiResolver) {
        this.i18n = i18n;
        this.emojiResolver = emojiResolver;
    }

    /// First resolves the variables in the given message (see [PlaceholderResolver#resolve(String, Map)]), then
    /// localizes the resulting message (see [I18n#localize(Locale, String, Entry...)]) and lastly attempts to
    /// resolve emojis (see [EmojiResolver#resolve(String)]).
    ///
    /// @param message the message to be resolved
    /// @param locale the locale to use for i18n
    /// @param placeholder the placeholders to use for i18n
    ///
    /// @return the resolved message
    public String resolve(String message, Locale locale, Map<String, @Nullable Object> placeholder) {
        String formatted = PlaceholderResolver.resolve(message, placeholder);
        String localized = i18n.localize(locale, formatted, placeholder);
        return emojiResolver.resolve(localized);
    }

    /// First resolves the variables in the given message (see [PlaceholderResolver#resolve(String, Map)]), then
    /// localizes the resulting message (see [I18n#localize(Locale, String, Entry...)]) and lastly attempts to
    /// resolve emojis (see [EmojiResolver#resolve(String)]).
    ///
    /// @param message the message to be resolved
    /// @param locale the locale to use for i18n
    /// @param placeholder the placeholders to use for i18n
    ///
    /// @return the resolved message
    public String resolve(String message, Locale locale, Entry... placeholder) {
        return resolve(message, locale, Entry.toMap(placeholder));
    }

    /// Gets the underlying [I18n] instance
    ///
    /// @return the used [I18n] instance
    public I18n i18n() {
        return i18n;
    }

    /// Gets the underlying [EmojiResolver] instance
    ///
    /// @return the used [EmojiResolver] instance
    public EmojiResolver emojiResolver() {
        return emojiResolver;
    }
}
