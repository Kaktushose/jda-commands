package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.internal.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import org.jetbrains.annotations.ApiStatus;

/// A collection of classes relevant for [EventHandler]s.
@ApiStatus.Internal
public record DispatchingContext(MiddlewareRegistry middlewareRegistry,
                                 ImplementationRegistry implementationRegistry,
                                 InteractionRegistry registry,
                                 TypeAdapterRegistry adapterRegistry,
                                 ExpirationStrategy expirationStrategy) {
}
