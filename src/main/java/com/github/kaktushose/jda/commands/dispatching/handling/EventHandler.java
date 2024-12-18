package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.handling.command.ContextCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.command.SlashCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SequencedCollection;
import java.util.function.BiConsumer;

public abstract sealed class EventHandler<T extends GenericInteractionCreateEvent> implements BiConsumer<T, Runtime>
        permits AutoCompleteHandler, ContextCommandHandler, SlashCommandHandler, ButtonHandler {

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

    protected abstract InvocationContext<T> prepare(T event, Runtime runtime);

    @Override
    final public void accept(T e, Runtime runtime) {
        InvocationContext<T> context = prepare(e, runtime);

        if (context == null || Thread.interrupted()) {
            log.debug("Interaction execution cancelled by preparation task");
            return;
        }

        log.debug("Executing middlewares...");
        middlewareRegistry.forAllOrdered(middleware -> {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.accept(context);
        });

        if (Thread.interrupted()) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        context.definition().invoke(context);
    }

    protected final InvocationContext<T> newContext(T e, Runtime runtime, GenericInteractionDefinition definition, SequencedCollection<Object> arguments) {
        return new InvocationContext<>(
                e,
                runtime.keyValueStore(),
                definition,
                arguments,
                runtime.instanceSupplier(),
                handlerContext
        );
    }
}
