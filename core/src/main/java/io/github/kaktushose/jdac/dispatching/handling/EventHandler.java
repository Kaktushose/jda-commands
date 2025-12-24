package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.InternalProperties;
import io.github.kaktushose.jdac.configuration.internal.Properties;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.handling.command.ContextCommandHandler;
import io.github.kaktushose.jdac.dispatching.handling.command.SlashCommandHandler;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
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

    public static final ScopedValue<Introspection> INTROSPECTION = ScopedValue.newInstance();

    public static final ScopedValue<Boolean> INVOCATION_PERMITTED = ScopedValue.newInstance();

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final Resolver resolver;
    protected final Middlewares middlewares;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapters adapterRegistry;
    protected final ErrorMessageFactory errorMessageFactory;

    public EventHandler(Resolver resolver) {
        this.resolver = resolver;

        this.middlewares = resolver.get(InternalProperties.MIDDLEWARES);
        this.interactionRegistry = resolver.get(InternalProperties.INTERACTION_REGISTRY);
        this.adapterRegistry = resolver.get(InternalProperties.TYPE_ADAPTERS);
        this.errorMessageFactory = resolver.get(Property.ERROR_MESSAGE_FACTORY);
    }

    @Nullable
    protected abstract InvocationContext<T> prepare(T event, Runtime runtime);

    @Override
    public final void accept(T e, Runtime runtime) {
        log.debug("Got event {}", e);

        InvocationContext<T> invocationContext = prepare(e, runtime);

        if (invocationContext == null || Thread.interrupted()) {
            log.debug("Interaction execution cancelled by preparation task");
            return;
        }

        Resolver interactionResolver = Properties.Builder.newRestricted()
                .addFallback(Property.JDA_EVENT, _ -> e)
                .addFallback(Property.INVOCATION_CONTEXT, _ -> invocationContext)
                .createResolver(this.resolver);

        Introspection introspection = new Introspection(interactionResolver, Stage.INTERACTION);

        ScopedValue.where(INTROSPECTION, introspection).run(() -> {
            log.debug("Executing middlewares...");
            middlewares.forOrdered(invocationContext.definition().classDescription().clazz(), middleware -> {
                log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
                middleware.accept(invocationContext);
            });

            if (Thread.interrupted()) {
                log.debug("Interaction execution cancelled by middleware");
                return;
            }

            invoke(invocationContext, runtime);
        });
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
            Object instance = runtime.interactionInstance(definition.classDescription().clazz(), Introspection.access());

            ScopedValue.where(INVOCATION_PERMITTED, true).call(() -> definition.invoke(instance, invocation));
        } catch (Exception exception) {
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            log.error("Interaction execution failed!", throwable);

            // 10062 is the error code for "Unknown interaction". In that case we cannot send any reply, not even the
            // error message.
            if (throwable instanceof ErrorResponseException errorResponse && errorResponse.getErrorCode() == 10062) {
                return;
            }

            // if the throwing event is a component event we should remove the component to prevent further executions
            boolean deleted = false;
            if (invocation.event() instanceof GenericComponentInteractionCreateEvent componentEvent) {
                var message = componentEvent.getMessage();
                // ugly workaround to check if the message is still valid after removing components or if we have to delete
                // the entire message
                var data = new MessageCreateBuilder().applyMessage(componentEvent.getMessage());
                data.setComponents();
                if (data.isValid()) {
                    message.editMessageComponents().complete();
                } else {
                    deleted = true;
                    message.delete().complete();
                }
            }

            invocation.cancel(errorMessageFactory.getCommandExecutionFailedMessage(invocation, throwable));

            if (invocation.event() instanceof IReplyCallback callback && !deleted) {
                callback.getHook().editOriginalComponents().queue();
            }
        }
    }
}
