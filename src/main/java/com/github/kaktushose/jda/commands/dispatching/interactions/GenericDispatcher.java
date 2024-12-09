package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.modals.ModalDispatcher;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for event dispatchers.
 *
 * @since 4.0.0
 */
public abstract sealed class GenericDispatcher<T extends GenericInteractionCreateEvent>
        permits AutoCompleteDispatcher, CommandDispatcher, ComponentDispatcher, ModalDispatcher {

    private final static Logger log = LoggerFactory.getLogger(GenericDispatcher.class);

    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;
    protected final RuntimeSupervisor runtimeSupervisor;

    public GenericDispatcher(HandlerContext handlerContext) {
        this.middlewareRegistry = handlerContext.middlewareRegistry();
        this.implementationRegistry = handlerContext.implementationRegistry();
        this.interactionRegistry = handlerContext.interactionRegistry();
        this.adapterRegistry = handlerContext.adapterRegistry();
        this.runtimeSupervisor = handlerContext.runtimeSupervisor();
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

    public abstract void onEvent(T event, Runtime runtime);

}
