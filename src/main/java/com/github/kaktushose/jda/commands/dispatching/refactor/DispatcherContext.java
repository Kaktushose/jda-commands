package com.github.kaktushose.jda.commands.dispatching.refactor;

import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record DispatcherContext(MiddlewareRegistry middlewareRegistry,
                                ImplementationRegistry implementationRegistry,
                                InteractionRegistry interactionRegistry,
                                TypeAdapterRegistry adapterRegistry,
                                RuntimeSupervisor runtimeSupervisor) {
}
