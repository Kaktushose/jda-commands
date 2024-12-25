package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.jetbrains.annotations.ApiStatus;

/// A collection of classes relevant for [EventHandler]s.
@ApiStatus.Internal
public record DispatchingContext(MiddlewareRegistry middlewareRegistry,
                                 ImplementationRegistry implementationRegistry,
                                 InteractionRegistry interactionRegistry,
                                 TypeAdapterRegistry adapterRegistry,
                                 ExpirationStrategy expirationStrategy) {
}
