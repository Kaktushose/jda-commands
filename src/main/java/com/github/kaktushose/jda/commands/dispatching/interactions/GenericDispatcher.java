package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for event dispatchers.
 *
 * @since 4.0.0
 */
public abstract class GenericDispatcher {

    private final static Logger log = LoggerFactory.getLogger(GenericDispatcher.class);

    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;
    protected final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new GenericDispatcher.
     */
    public GenericDispatcher(MiddlewareRegistry middlewareRegistry,
                             ImplementationRegistry implementationRegistry,
                             InteractionRegistry interactionRegistry,
                             TypeAdapterRegistry adapterRegistry,
                             RuntimeSupervisor runtimeSupervisor) {
        this.middlewareRegistry = middlewareRegistry;
        this.implementationRegistry = implementationRegistry;
        this.interactionRegistry = interactionRegistry;
        this.adapterRegistry = adapterRegistry;
        this.runtimeSupervisor = runtimeSupervisor;
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

    /**
     * Dispatches a {@link Context}.
     *
     * @param context the {@link Context} to dispatch.
     */
    public abstract void onEvent(Context context);

}
