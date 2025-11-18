package io.github.kaktushose.jdac.extension;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Instances of [Implementation] are used to provide custom implementations of [ExtensionProvidable] interfaces, namely:
/// - [ClassFinder]
/// - [Descriptor]
/// - [InteractionControllerInstantiator]
/// - [ErrorMessageFactory]
/// - [MiddlewareContainer] (wrapper type for [Middleware])
/// - [TypeAdapterContainer] (wrapper type for [TypeAdapter])
/// - [ValidatorContainer] (wrapper type for [Validator])
/// - [PermissionsProvider]
/// - [GuildScopeProvider]
///
/// Such instances of [Implementation] are returned by [Extension#providedImplementations()] and used by the [JDACBuilder] to
/// configure jda-commands.
///
/// **Note: If the [#supplier()] returns an empty collection, then this [Implementation] is discarded and thus treated as non-existent.**
///
/// @param type     the [Class] of the implemented interface
/// @param supplier the [Function] used to retrieve instances of the custom implementation
/// @see Extension
public record Implementation<T extends Implementation.ExtensionProvidable>(
        Class<T> type,
        Function<JDACBuilderDataOld, SequencedCollection<T>> supplier
) {

    public static <T extends ExtensionProvidable> Implementation<T> single(Class<T> type,
                                                                           Function<JDACBuilderDataOld,
                                                                           T> supplier) {
        return new Implementation<>(type, (builder -> List.of(supplier.apply(builder))));
    }

    SequencedCollection<T> implementations(JDACBuilderDataOld data) {
        if (data.alreadyCalled.stream().anyMatch(provider -> provider.type.equals(type))) {
            throw new ConfigurationException("cycling-dependencies", entry("type", type.getName()), entry("data", format(data)));
        }

        data.alreadyCalled.add(this); // scope entry

        // other suppliers could be called here
        // Scoping this will create a simple stack of already called methods, allowing checking for cycling dependencies (Implementation#type())
        SequencedCollection<T> apply = supplier().apply(data);

        data.alreadyCalled.remove(this); // scope leave
        return apply;
    }

    private String format(JDACBuilderDataOld data) {
        List<GraphEntry> stack = data.alreadyCalled.reversed().stream()
                .map(provider -> {
                    var extension = data.implementations(provider.type).stream().findAny().map(Map.Entry::getKey).orElseThrow().getClass();
                    return new GraphEntry(extension, provider.type);
                })
                .toList();

        if (stack.size() == 1) {
            GraphEntry entry = stack.getFirst();
            return "%s provides and needs %s, thus calls itself".formatted(entry.extension.getSimpleName(), entry.provides.getSimpleName());
        }

        List<String> lines = stack
                .stream()
                .flatMap(graphEntry -> Stream.of("↓", "%s defines %s".formatted(graphEntry.extension.getSimpleName(), graphEntry.provides.getSimpleName())))
                .skip(1)
                .collect(Collectors.toList());


        int intend = lines.stream().map(String::length).max(Integer::compare).orElseThrow() + 3;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int missing = intend - line.length();
            String appendix;
            if (i == 0) {
                appendix = " ".repeat(missing) + "←--|";
            } else if (i == (lines.size() - 1)) {
                appendix = " ".repeat(missing) + "→--|";
            } else {
                appendix = " ".repeat(missing + 3) + "|";
            }
            lines.set(i, line + appendix);
        }
        return String.join(System.lineSeparator(), lines);
    }

    /// A marker interface that all types providable by an [Extension] share.
    public sealed interface ExtensionProvidable permits ClassFinder, Descriptor, InteractionControllerInstantiator, ErrorMessageFactory, ProvidableContainer, EmojiSource, Localizer, PermissionsProvider, GuildScopeProvider {}
    public sealed interface ProvidableContainer extends ExtensionProvidable {}

    /// A container type for providing [TypeAdapter]s.
    ///
    /// @param source  the [Type] of the value to convert
    /// @param target  the [Type] to convert into
    /// @param adapter the [TypeAdapter] implementation
    /// @param <S>     the source type
    /// @param <T>     the target type
    public record TypeAdapterContainer<S, T>(Type<S> source,
                                             Type<T> target,
                                             TypeAdapter<S, T> adapter) implements ProvidableContainer {}

    /// A container type for providing [Middleware]s.
    ///
    /// @param priority   the [Priority] with which the [Middleware] should be registered
    /// @param middleware the [Middleware] implementation
    public record MiddlewareContainer(Priority priority,
                                      Middleware middleware) implements ProvidableContainer {}

    /// A container type for providing [Validator]s.
    ///
    /// @param annotation the [Annotation] for which the [Validator] should be registered
    /// @param validator  the [Validator] implementation
    public record ValidatorContainer(Class<? extends Annotation> annotation,
                                     Validator<?, ?> validator) implements ProvidableContainer {}

    private record GraphEntry(Class<?> extension, Class<?> provides) {}
}
