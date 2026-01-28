package io.github.kaktushose.jdac.dispatching.handling;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.InternalProperties;
import io.github.kaktushose.jdac.configuration.internal.Properties;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.handling.command.ContextCommandHandler;
import io.github.kaktushose.jdac.dispatching.handling.command.SlashCommandHandler;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.dispatching.reply.internal.ReplyAction;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import io.github.kaktushose.jdac.introspection.lifecycle.events.InteractionStartEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
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
/// 3. Invocation ([EventHandler#invoke(InvocationContext, Runtime, IntrospectionImpl)]):
/// In this step the user implemented method is called with help of the right [InteractionDefinition]
@ApiStatus.Internal
public abstract sealed class EventHandler<T extends GenericInteractionCreateEvent>
        implements BiConsumer<T, Runtime>
        permits AutoCompleteHandler, ComponentHandler, ModalHandler, ContextCommandHandler, SlashCommandHandler {

    public static final ScopedValue<Boolean> INVOCATION_PERMITTED = ScopedValue.newInstance();

    public static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    protected final IntrospectionImpl runtimeIntrospection;
    protected final InteractionRegistry interactionRegistry;
    protected final ErrorMessageFactory errorMessageFactory;

    public EventHandler(IntrospectionImpl runtimeIntrospection) {
        this.runtimeIntrospection = runtimeIntrospection;

        this.interactionRegistry = runtimeIntrospection.get(InternalProperties.INTERACTION_REGISTRY);
        this.errorMessageFactory = runtimeIntrospection.get(Property.ERROR_MESSAGE_FACTORY);
    }

    @Nullable protected abstract PreparationResult prepare(T event, Runtime runtime);

    @Override
    public final void accept(T e, Runtime runtime) {
        log.debug("Got event {}", e);

        IntrospectionImpl preparationIntrospection = Properties.Builder.newRestricted()
                .addFallback(Property.JDA_EVENT, _ -> e)
                .createIntrospection(this.runtimeIntrospection, Stage.PREPARATION);

        PreparationResult preparationResult = ScopedValue.where(IntrospectionImpl.INTROSPECTION, preparationIntrospection)
                .call(() -> prepare(e, runtime));

        if (preparationResult == null || Thread.interrupted()) {
            log.debug("Interaction execution cancelled by preparation task");
            return;
        }

        InvocationContext<T> invocationContext =
                new InvocationContext<>(e, runtime.keyValueStore(), preparationResult.definition,
                        Helpers.replyConfig(preparationResult.definition, runtimeIntrospection.get(Property.GLOBAL_REPLY_CONFIG)),
                        preparationResult.rawArguments);

        IntrospectionImpl interactionIntrospection = Properties.Builder.newRestricted()
                .addFallback(Property.JDA_EVENT, _ -> e)
                .addFallback(Property.INVOCATION_CONTEXT, _ -> invocationContext)
                .createIntrospection(preparationIntrospection, Stage.INTERACTION);

        ScopedValue.where(IntrospectionImpl.INTROSPECTION, interactionIntrospection).run(() -> {
            log.debug("Executing middlewares...");

            Middlewares middlewares = Introspection.scopedGet(InternalProperties.MIDDLEWARES);
            middlewares.forOrdered(invocationContext.definition().classDescription().clazz(), middleware -> {
                log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
                middleware.accept(invocationContext);
            });

            if (Thread.interrupted()) {
                log.debug("Interaction execution cancelled by middleware");
                return;
            }

            invoke(invocationContext, runtime, interactionIntrospection);
        });
    }

    private void invoke(InvocationContext<T> invocation, Runtime runtime, IntrospectionImpl introspection) {
        SequencedCollection<@Nullable Object> arguments = invocation.arguments();

        var definition = invocation.definition();

        log.info("Executing interaction \"{}\" for user \"{}\"", definition.displayName(), invocation.event().getUser().getEffectiveName());
        try {
            introspection.publish(new InteractionStartEvent(invocation));

            if (Thread.interrupted()) {
                log.debug("Interaction execution cancelled by InteractionStartEvent subscriber");
                return;
            }

            log.debug("Invoking method \"{}.{}\" with following arguments: {}",
                    definition.classDescription().name(),
                    definition.methodDescription().name(),
                    arguments
            );
            Object instance = runtime.interactionInstance(definition.classDescription().clazz());

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

            boolean edit = invocation.replyConfig().editReply();
            if (invocation.event() instanceof GenericComponentInteractionCreateEvent componentEvent && !edit) {
                Message message = componentEvent.getMessage();
                if (Helpers.isValidWithoutComponents(message)) {
                    message.editMessageComponents().queue();
                } else {
                    edit = true;
                }
            }

            // we don't call invocation#cancel here so we have control over the edit behavior. If removing the component
            // would make the message invalid we just replace it with the error message
            ReplyConfig replyConfig = invocation.replyConfig();
            new ReplyAction(new ReplyConfig(
                    replyConfig.ephemeral(),
                    false,
                    false,
                    edit,
                    replyConfig.silent(),
                    replyConfig.allowedMentions()
            )
            ).reply(errorMessageFactory.getInteractionExecutionFailedMessage(invocation, throwable));
        }
    }

    public record PreparationResult(InteractionDefinition definition, SequencedCollection<@Nullable Object> rawArguments) {}
}
