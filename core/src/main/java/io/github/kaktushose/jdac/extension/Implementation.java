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
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;

import java.lang.annotation.Annotation;

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
/// @see Extension
public record Implementation<T extends Implementation.ExtensionProvidable>(
        Class<T> type
) {

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
