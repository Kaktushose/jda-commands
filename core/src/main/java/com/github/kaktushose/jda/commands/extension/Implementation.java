package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.i18n.Localizer;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Instances of [Implementation] are used to provide custom implementations of [ExtensionProvidable] interfaces, namely:
/// - [ClassFinder]
/// - [Descriptor]
/// - [InteractionControllerInstantiator]
/// - [ErrorMessageFactory]
/// - [MiddlewareContainer] (wrapper type for [Middleware])
/// - [TypeAdapterContainer] (wrapper type for [TypeAdapter])
/// - [ValidatorContainer] (wrapper type for [Validator])
/// - [EmbedConfigurationContainer] (wrapper type for [EmbedConfig])
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
        @NotNull Class<T> type,
        @NotNull Function<@NotNull JDACBuilderData, @NotNull SequencedCollection<@NotNull T>> supplier
) {

    public static <T extends ExtensionProvidable> Implementation<T> single(@NotNull Class<T> type,
                                                                           @NotNull Function<@NotNull JDACBuilderData,
                                                                           @NotNull T> supplier) {
        return new Implementation<>(type, (builder -> List.of(supplier.apply(builder))));
    }

    SequencedCollection<T> implementations(JDACBuilderData data) {
        if (data.alreadyCalled.stream().anyMatch(provider -> provider.type.equals(type))) {
            throw new JDACBuilder.ConfigurationException(
                    "Cycling dependencies while getting implementations of %s! \n%s".formatted(type, format(data))
            );
        }

        data.alreadyCalled.add(this); // scope entry

        // other suppliers could be called here
        // Scoping this will create a simple stack of already called methods, allowing checking for cycling dependencies (Implementation#type())
        SequencedCollection<T> apply = supplier().apply(data);

        data.alreadyCalled.remove(this); // scope leave
        return apply;
    }

    private String format(JDACBuilderData data) {
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
    public sealed interface ExtensionProvidable permits ClassFinder, Descriptor, InteractionControllerInstantiator, ErrorMessageFactory, ProvidableContainer, EmbedConfigurationContainer, Localizer, PermissionsProvider, GuildScopeProvider {}
    public sealed interface ProvidableContainer extends ExtensionProvidable {}

    /// A container type for providing [TypeAdapter]s.
    ///
    /// @param source the [Type] of the value to convert
    /// @param target the [Type] to convert into
    /// @param adapter the [TypeAdapter] implementation
    /// @param <S>    the source type
    /// @param <T>    the target type
    public record TypeAdapterContainer<S, T>(@NotNull Type<S> source,
                                             @NotNull Type<T> target,
                                             @NotNull TypeAdapter<S, T> adapter) implements ProvidableContainer {}

    /// A container type for providing [Middleware]s.
    ///
    /// @param priority   the [Priority] with which the [Middleware] should be registered
    /// @param middleware the [Middleware] implementation
    public record MiddlewareContainer(@NotNull Priority priority,
                                      @NotNull Middleware middleware) implements ProvidableContainer {}

    /// A container type for providing [Validator]s.
    ///
    /// @param annotation the [Annotation] for which the [Validator] should be registered
    /// @param validator  the [Validator] implementation
    public record ValidatorContainer(@NotNull Class<? extends Annotation> annotation,
                                     @NotNull Validator<?, ?> validator) implements ProvidableContainer {}

    /// A container type for providing an [EmbedConfig].
    ///
    /// @param config the [Consumer] accepting the [EmbedConfig]
    public record EmbedConfigurationContainer(Consumer<EmbedConfig> config) implements ExtensionProvidable {}


    private record GraphEntry(Class<?> extension, Class<?> provides) {}
}
