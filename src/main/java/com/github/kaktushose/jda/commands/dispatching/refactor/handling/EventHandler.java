package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public abstract class EventHandler<T extends GenericInteractionCreateEvent, E extends ExecutionContext<T, ?>> implements BiConsumer<T, Runtime> {

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final HandlerContext handlerContext;
    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;
    protected final RuntimeSupervisor runtimeSupervisor;

    public EventHandler(HandlerContext handlerContext) {
        this.handlerContext = handlerContext;
        this.middlewareRegistry = handlerContext.middlewareRegistry();
        this.implementationRegistry = handlerContext.implementationRegistry();
        this.interactionRegistry = handlerContext.interactionRegistry();
        this.adapterRegistry = handlerContext.adapterRegistry();
        this.runtimeSupervisor = handlerContext.runtimeSupervisor();
    }

    protected abstract E prepare(T event, Runtime runtime);
    protected abstract void execute(E context);

    @Override
    final public void accept(T e, Runtime runtime) {
        E context = prepare(e, runtime);

        if (context == null || context.cancelled()) {
            log.debug("Interaction execution cancelled by preparation task");
            return;
        }

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        execute(context, runtime);
    }

    protected void executeMiddlewares(ExecutionContext<? extends GenericInteractionCreateEvent, ? extends GenericInteractionDefinition> context) {
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

    @SuppressWarnings("DataFlowIssue")
    protected boolean checkCancelled(ExecutionContext<? extends GenericInteractionCreateEvent, ? extends GenericInteractionDefinition> context) {
        if (context.cancelled()) {
            // ReplyContext replyContext = new ReplyContext(context);
            // replyContext.getBuilder().applyData(context.errorMessage());
            // replyContext.queue();
            return true;
        }
        return false;
    }

    private boolean executeMiddleware(ExecutionContext<? extends GenericInteractionCreateEvent, ? extends GenericInteractionDefinition> context, Middleware middleware) {
        log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
        // middleware.accept(context);
        return context.cancelled();
    }
}
