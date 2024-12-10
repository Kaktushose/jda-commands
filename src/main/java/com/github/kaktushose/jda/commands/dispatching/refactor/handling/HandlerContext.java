package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record HandlerContext(MiddlewareRegistry middlewareRegistry,
                             ImplementationRegistry implementationRegistry,
                             InteractionRegistry interactionRegistry,
                             TypeAdapterRegistry adapterRegistry) {
}
