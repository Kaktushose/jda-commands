package io.github.kaktushose.jdac.message.resolver;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// The MessageResolver combines all registered [`Resolver<String>`][Property#STRING_RESOLVER]
/// and following default resolvers:
///
/// 1. [PlaceholderResolver] (priority = 1000)
/// 2. [I18n] (priority = 2000)
/// 3. [EmojiResolver] (priority = 3000)
///
/// [Resolver]s with lower [`priority`][Resolver#priority()] will be executed first, e.g. [PlaceholderResolver] will
/// run before [I18n].
///
/// Please note that this class is a helper and doesn't have own resolving logic, it's more of a pipeline
/// of all string resolvers. It is not intended to be directly used by end users but part of the public api
/// to allow manual execution of the frameworks resolving logic for dynamic values if needed.
public final class MessageResolver implements Resolver<String> {

    private final Collection<Resolver<String>> resolvers;

    public MessageResolver(Collection<Resolver<String>> resolvers) {
        this.resolvers = new TreeSet<>(Comparator.comparingInt(Resolver::priority));
        this.resolvers.addAll(resolvers);
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
    @Override
    public String resolve(String message, Locale locale, Map<String, @Nullable Object> placeholder) {
        String content = message;
        for (Resolver<String> resolver : resolvers) {
            content = resolver.resolve(content, locale, placeholder);
        }

        return content;
    }

    /// @return 0
    @Override
    public int priority() {
        return 0;
    }
}
