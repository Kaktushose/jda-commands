package com.github.kaktushose.jda.commands.message;

import com.github.kaktushose.jda.commands.message.i18n.I18n;
import com.github.kaktushose.jda.commands.message.emoji.EmojiResolver;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/// The MessageResolver combines [I18n#localize(Locale, String, I18n.Entry...)] and
/// [EmojiResolver#resolve(String)].
///
/// It will first do localization with help of [I18n] and then apply the returned string
/// to [EmojiResolver] to resolve Unicode aliases and application emojis.
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

    /// First localizes the given message (see [I18n#localize(Locale, String, I18n.Entry...)]) and then attempts to
    /// resolve emojis (see [EmojiResolver#resolve(String)]).
    ///
    /// @param message the message to be resolved
    /// @param locale the locale to use for i18n
    /// @param placeholder the placeholders to use for i18n
    ///
    /// @return the resolved message
    public String resolve(String message, Locale locale, Map<String, @Nullable Object> placeholder) {
        String localized = i18n.localize(locale, message, placeholder);
        return emojiResolver.resolve(localized);
    }

    /// First localizes the given message (see [I18n#localize(Locale, String, I18n.Entry...)]) and then attempts to
    /// resolve emojis (see [EmojiResolver#resolve(String)]).
    ///
    /// @param message the message to be resolved
    /// @param locale the locale to use for i18n
    /// @param placeholder the placeholders to use for i18n
    ///
    /// @return the resolved message
    public String resolve(String message, Locale locale, I18n.Entry... placeholder) {
        String localized = i18n.localize(locale, message, placeholder);
        return emojiResolver.resolve(localized);
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
