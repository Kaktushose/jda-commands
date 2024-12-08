package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.refactor.DispatcherContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.Event;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public abstract class EventHandler<T extends Event> implements BiConsumer<T, Runtime> {

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;
    protected final RuntimeSupervisor runtimeSupervisor;

    public EventHandler(DispatcherContext dispatcherContext) {
        this.middlewareRegistry = dispatcherContext.middlewareRegistry();
        this.implementationRegistry = dispatcherContext.implementationRegistry();
        this.interactionRegistry = dispatcherContext.interactionRegistry();
        this.adapterRegistry = dispatcherContext.adapterRegistry();
        this.runtimeSupervisor = dispatcherContext.runtimeSupervisor();
    }

    protected void executeMiddlewares(Context context) {
        log.debug("Executing middlewares...");
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.PERMISSIONS)) {
            if (executeMiddleware(context, middleware)) return;
        }
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.HIGH)) {
            if (executeMiddleware(context, middleware)) return;
        }
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.NORMAL)) {
            if (executeMiddleware(context, middleware)) return;
        }
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.LOW)) {
            if (executeMiddleware(context, middleware)) return;
        }
    }

    private boolean executeMiddleware(Context context, Middleware middleware) {
        log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
        middleware.accept(context);
        return context.isCancelled();
    }
}
