package io.github.kaktushose.jdac.dispatching.events;

import io.github.kaktushose.jdac.annotations.interactions.Modal;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.reply.dynamic.ModalBuilder;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Subtype of [ReplyableEvent] that also supports replying with a [Modal].
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see CommandEvent
/// @see ComponentEvent
public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent>
        extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    private static final Logger log = LoggerFactory.getLogger(ModalReplyableEvent.class);

    /// Constructs a new ModalReplyableEvent.
    ///
    /// @param event       the subtype [T] of [GenericInteractionCreateEvent]
    /// @param registry    the corresponding [InteractionRegistry]
    /// @param runtime     the [Runtime] this event lives in
    /// @param definition  the [InteractionDefinition] this event belongs to
    /// @param replyConfig the [InteractionDefinition.ReplyConfig] to use
    /// @param embeds      the corresponding [Embeds]
    protected ModalReplyableEvent(T event,
                                  InteractionRegistry registry,
                                  Runtime runtime,
                                  InteractionDefinition definition,
                                  InteractionDefinition.ReplyConfig replyConfig,
                                  Embeds embeds
    ) {
        super(event, registry, runtime, definition, replyConfig, embeds);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param modal       the method name of the [Modal] you want to reply with
    /// @param placeholder the [Entry] placeholders to use for localization
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, Entry... placeholder) {
        replyModal(modal, builder -> builder.placeholder(placeholder));
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param modal    the method name of the [Modal] you want to reply with
    /// @param callback a [Function] to dynamically modify the [Modal] before replying with it
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, Function<ModalBuilder, ModalBuilder> callback) {
        if (event instanceof IModalCallback modalCallback) {
            var definitionId = String.valueOf((definition.classDescription().name() + modal).hashCode());
            var modalDefinition = registry.find(ModalDefinition.class, false, it ->
                    it.definitionId().equals(definitionId)
            );
            var builtModal = callback.apply(new ModalBuilder(this, new CustomId(runtimeId(), definitionId), modalDefinition)).build();

            log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.displayName(), modalDefinition.displayName(), runtimeId());
            modalCallback.replyModal(builtModal).queue();
        } else {
            throw new InternalException("reply-failed", entry("event", event.getClass().getName()));
        }
    }
}
