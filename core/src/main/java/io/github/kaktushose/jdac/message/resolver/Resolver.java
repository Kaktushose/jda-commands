package io.github.kaktushose.jdac.message.resolver;

import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;


/// Common interface for all resolvers of JDA-Commands.
///
/// A Resolver takes an arbitrary object and transforms it to a state, where it can be sent to the Discord API. For
/// instance, the [MessageResolver] will take a String as input and then apply placeholders, localization and resolve
/// any emoji references. Other Resolver implementations are responsible for more complex objects,
/// like [components][ComponentResolver].
///
/// Most Resolvers are not intended to be directly used by end users but part of the public api to allow manual
/// execution
/// of the frameworks resolving logic for dynamic values if needed.
///
/// @param <T> the type to resolve
/// @see MessageResolver
/// @see DataObjectResolver
/// @see ComponentResolver
public interface Resolver<T> {

    /// Resolves the given object for the provided locale.
    ///
    /// @param object       the object to resolve
    /// @param locale       the [Locale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used localization system
    /// @return the resolved object
    T resolve(T object, Locale locale, Map<String, @Nullable Object> placeholders);

    /// Resolves the given object for the provided locale.
    ///
    /// @param object       the object to resolve
    /// @param locale       the [Locale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used localization system
    /// @return the resolved object
    default T resolve(T object, Locale locale, Entry... placeholders) {
        return resolve(object, locale, Entry.toMap(placeholders));
    }

    /// Resolves the given object for the provided locale.
    ///
    /// @param object       the object to resolve
    /// @param locale       the [DiscordLocale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used localization system
    /// @return the resolved object
    default T resolve(T object, DiscordLocale locale, Map<String, @Nullable Object> placeholders) {
        return resolve(object, locale.toLocale(), placeholders);
    }

    /// Resolves the given object for the provided locale.
    ///
    /// @param object       the object to resolve
    /// @param locale       the [DiscordLocale] to use for localization
    /// @param placeholders the placeholders to use if supported by the used localization system
    /// @return the resolved object
    default T resolve(T object, DiscordLocale locale, Entry... placeholders) {
        return resolve(object, locale, Entry.toMap(placeholders));
    }

    /// The priority of this resolver influences the order in which resolver are applied in a resolution pipeline.
    ///
    /// Currently, this only applies to `Resolver<String>`, when using [MessageResolver] (as JDA-Commands does
    /// internally).
    /// Generally speaking, resolvers with lower priority run first.
    ///
    /// If the priority isn't important (e.g. for [ComponentResolver]) this should return `0`.
    ///
    /// @return the priority of this resolver
    int priority();

}
