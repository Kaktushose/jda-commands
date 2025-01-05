package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import org.jetbrains.annotations.ApiStatus;

/// A collection of classes relevant for [EventHandler]s.
@ApiStatus.Internal
public record DispatchingContext(MiddlewareRegistry middlewareRegistry,
                                 ErrorMessageFactory errorMessageFactory,
                                 InteractionRegistry registry,
                                 TypeAdapterRegistry adapterRegistry,
                                 ExpirationStrategy expirationStrategy,
                                 DependencyInjector dependencyInjector) {
}
