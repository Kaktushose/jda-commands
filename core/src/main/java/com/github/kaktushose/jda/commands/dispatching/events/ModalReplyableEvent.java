package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.Modal;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.ModalBuilder;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/// Subtype of [ReplyableEvent] that also supports replying with a [Modal].
///
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
    /// @param embeds     the corresponding [Embeds]
    protected ModalReplyableEvent(T event,
                                  InteractionRegistry registry,
                                  Runtime runtime,
                                  InteractionDefinition definition,
                                  InteractionDefinition.ReplyConfig replyConfig,
                                  Embeds embeds
    ) {
        super(event, registry, runtime, definition, replyConfig, embeds);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target user's Discord client.
    ///
    /// @param modal the method name of the [Modal] you want to reply with
    /// @param placeholder the [I18n.Entry] placeholders to use for localization
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, I18n.Entry... placeholder) {
        replyModal(modal, builder -> builder.placeholder(placeholder));
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target user's Discord client.
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
            throw new InternalException("Cannot reply to '%s'!", event.getClass().getName());
        }
    }
}
