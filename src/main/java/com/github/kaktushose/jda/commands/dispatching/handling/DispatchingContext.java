package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import org.jetbrains.annotations.ApiStatus;

/// A collection of classes relevant for [EventHandler]s.
@ApiStatus.Internal
public record DispatchingContext(Middlewares middlewares,
                                 ErrorMessageFactory errorMessageFactory,
                                 InteractionRegistry registry,
                                 TypeAdapters adapterRegistry,
                                 ExpirationStrategy expirationStrategy,
                                 DependencyInjector dependencyInjector) {
}
