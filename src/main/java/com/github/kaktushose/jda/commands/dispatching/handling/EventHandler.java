package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.handling.command.ContextCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.command.SlashCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.internal.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.SequencedCollection;
import java.util.function.BiConsumer;

/// Implementations of this class are handling specific [GenericInteractionCreateEvent]s.
///
/// Each [EventHandler] is split into 3 steps:
/// 1. Preparation ([EventHandler#prepare(GenericInteractionCreateEvent, Runtime)]):
/// In this step the [InvocationContext] is created from the jda event and the involved [Runtime].
///
/// 2. Middleware execution: In this step all registered [Middleware]s
/// are executed ordered by their [Priority].
///
/// 3. Invocation ([EventHandler#invoke(InvocationContext, Runtime)]):
/// In this step the user implemented method is called with help of the right [GenericInteractionDefinition]
@ApiStatus.Internal
public abstract sealed class EventHandler<T extends GenericInteractionCreateEvent>
        implements BiConsumer<T, Runtime>
        permits AutoCompleteHandler, ComponentHandler, ModalHandler, ContextCommandHandler, SlashCommandHandler {

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final DispatchingContext dispatchingContext;
    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry registry;
    protected final TypeAdapterRegistry adapterRegistry;

    public EventHandler(DispatchingContext dispatchingContext) {
        this.dispatchingContext = dispatchingContext;
        this.middlewareRegistry = dispatchingContext.middlewareRegistry();
        this.implementationRegistry = dispatchingContext.implementationRegistry();
        this.registry = dispatchingContext.registry();
        this.adapterRegistry = dispatchingContext.adapterRegistry();
    }

    @Nullable
    protected abstract InvocationContext<T> prepare(@NotNull T event, @NotNull Runtime runtime);

    @Override
    public final void accept(T e, Runtime runtime) {
        log.debug("Got event {}", e);

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

        invoke(context, runtime);
    }

    private void invoke(@NotNull InvocationContext<T> invocation, @NotNull Runtime runtime) {
        SequencedCollection<Object> arguments = invocation.arguments();

        var definition = invocation.definition();

        log.info("Executing interaction \"{}\" for user \"{}\"", definition.displayName(), invocation.event().getUser().getEffectiveName());
        try {
            log.debug("Invoking method \"{}.{}\" with following arguments: {}",
                    definition.clazz().name(),
                    definition.method().name(),
                    arguments
            );
            Object instance = runtime.instance(definition);
            definition.invoke(instance, invocation);
        } catch (Exception exception) {
            log.error("Interaction execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            invocation.cancel(implementationRegistry.getErrorMessageFactory().getCommandExecutionFailedMessage(invocation.event(), throwable));
        }
    }
}
