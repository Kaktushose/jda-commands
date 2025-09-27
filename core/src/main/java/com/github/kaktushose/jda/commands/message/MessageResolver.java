package com.github.kaktushose.jda.commands.message;

import com.github.kaktushose.jda.commands.message.emoji.EmojiResolver;
import com.github.kaktushose.jda.commands.message.i18n.I18n;
import com.github.kaktushose.jda.commands.message.variables.PlaceholderResolver;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/// The MessageResolver combines [I18n#localize(Locale, String, I18n.Entry...)] and
/// [EmojiResolver#resolve(String)].
///
/// It will resolve the message in following order:
///
/// 1. resolve placeholders with help of [PlaceholderResolver]
/// 2. do localization with help of [I18n]
/// 3. resolve emojis with help [EmojiResolver]
///
///
/// Please note that this class is a helper and doesn't have ow resolving logic, it's morne of a pipeline
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
    /// localizes the resulting message (see [I18n#localize(Locale, String, I18n.Entry...)]) and lastly attempts to
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
    /// localizes the resulting message (see [I18n#localize(Locale, String, I18n.Entry...)]) and lastly attempts to
    /// resolve emojis (see [EmojiResolver#resolve(String)]).
    ///
    /// @param message the message to be resolved
    /// @param locale the locale to use for i18n
    /// @param placeholder the placeholders to use for i18n
    ///
    /// @return the resolved message
    public String resolve(String message, Locale locale, I18n.Entry... placeholder) {
        return resolve(message, locale, I18n.Entry.toMap(placeholder));
    }
}
