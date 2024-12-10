package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public abstract class EventHandler<T extends GenericInteractionCreateEvent> implements BiConsumer<T, Runtime> {

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final HandlerContext handlerContext;
    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;

    public EventHandler(HandlerContext handlerContext) {
        this.handlerContext = handlerContext;
        this.middlewareRegistry = handlerContext.middlewareRegistry();
        this.implementationRegistry = handlerContext.implementationRegistry();
        this.interactionRegistry = handlerContext.interactionRegistry();
        this.adapterRegistry = handlerContext.adapterRegistry();
    }

    protected abstract ExecutionContext<T> prepare(T event, Runtime runtime);


    protected void execute(ExecutionContext<T> context) {
        context.definition().invoke(context);
    }

    @Override
    final public void accept(T e, Runtime runtime) {
        ExecutionContext<T> context = prepare(e, runtime);

        if (context == null || Thread.interrupted()) {
            log.debug("Interaction execution cancelled by preparation task");
            return;
        }

        executeMiddlewares(context);
        if (Thread.interrupted()) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        execute(context);
    }

    protected void executeMiddlewares(ExecutionContext<T> context) {
        log.debug("Executing middlewares...");
        for (Middleware middleware : middlewareRegistry.getMiddlewares()) {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.accept(context);

            if (Thread.currentThread().isInterrupted()) return;
        }
    }
}
