package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.handling.command.ContextCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.command.SlashCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
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
/// In this step the user implemented method is called with help of the right [InteractionDefinition]
@ApiStatus.Internal
public abstract sealed class EventHandler<T extends GenericInteractionCreateEvent>
        implements BiConsumer<T, Runtime>
        permits AutoCompleteHandler, ComponentHandler, ModalHandler, ContextCommandHandler, SlashCommandHandler {

    // will be replaced with ScopedValues when available
    public static final ThreadLocal<Boolean> INVOCATION_PERMITTED = ThreadLocal.withInitial(() -> false);

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final DispatchingContext dispatchingContext;
    protected final Middlewares middlewares;
    protected final InteractionRegistry registry;
    protected final TypeAdapters adapterRegistry;
    protected final ErrorMessageFactory errorMessageFactory;

    public EventHandler(DispatchingContext dispatchingContext) {
        this.dispatchingContext = dispatchingContext;
        this.middlewares = dispatchingContext.middlewares();
        this.registry = dispatchingContext.registry();
        this.adapterRegistry = dispatchingContext.adapterRegistry();
        this.errorMessageFactory = dispatchingContext.errorMessageFactory();
    }

    @Nullable
    protected abstract InvocationContext<T> prepare(T event, Runtime runtime);

    @Override
    public final void accept(T e, Runtime runtime) {
        log.debug("Got event {}", e);

        InvocationContext<T> context = prepare(e, runtime);

        if (context == null || Thread.interrupted()) {
            log.debug("Interaction execution cancelled by preparation task");
            return;
        }

        log.debug("Executing middlewares...");
        middlewares.forOrdered(context.definition().classDescription().clazz(), middleware -> {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.accept(context);
        });

        if (Thread.interrupted()) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        invoke(context, runtime);
    }

    private void invoke(InvocationContext<T> invocation, Runtime runtime) {
        SequencedCollection<@Nullable Object> arguments = invocation.arguments();

        var definition = invocation.definition();

        log.info("Executing interaction \"{}\" for user \"{}\"", definition.displayName(), invocation.event().getUser().getEffectiveName());
        try {
            log.debug("Invoking method \"{}.{}\" with following arguments: {}",
                    definition.classDescription().name(),
                    definition.methodDescription().name(),
                    arguments
            );
            Object instance = runtime.interactionInstance(definition.classDescription().clazz());

            INVOCATION_PERMITTED.set(true);
            definition.invoke(instance, invocation);
        } catch (Exception exception) {
            log.error("Interaction execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;

            // if the throwing event is a component event we should remove the component to prevent further executions
            if (invocation.event() instanceof GenericComponentInteractionCreateEvent componentEvent) {
                var message = componentEvent.getMessage();
                // ugly workaround to check if the message is still valid after removing components or if we have to delete
                // the entire message
                var data = new MessageCreateBuilder().applyMessage(componentEvent.getMessage());
                data.setComponents();
                if (data.isValid()) {
                    message.editMessageComponents().complete();
                } else {
                    message.delete().complete();
                }
            }

            invocation.cancel(errorMessageFactory.getCommandExecutionFailedMessage(invocation, throwable));

            if (invocation.event() instanceof IReplyCallback callback) {
                callback.getHook().editOriginalComponents().queue();
            }
        }
    }
}
