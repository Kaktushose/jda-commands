package io.github.kaktushose.jdac.dispatching.events.interactions;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.ModalReplyableEvent;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;

/// This class is a subclass of [Event]. It provides additional features for replying to a [GenericComponentInteractionCreateEvent].
///
/// @see Event
/// @see ModalReplyableEvent
public final class ComponentEvent extends ModalReplyableEvent<GenericComponentInteractionCreateEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event       the [GenericComponentInteractionCreateEvent] this event holds
    /// @param registry    the corresponding [InteractionRegistry]
    /// @param runtime     the corresponding [Runtime]
    /// @param definition  the corresponding [InteractionDefinition]
    /// @param replyConfig the [InteractionDefinition.ReplyConfig] to use
    /// @param embeds      the corresponding [Embeds]
    public ComponentEvent(GenericComponentInteractionCreateEvent event,
                          InteractionRegistry registry,
                          Runtime runtime,
                          InteractionDefinition definition,
                          InteractionDefinition.ReplyConfig replyConfig,
                          Embeds embeds) {
        super(event, registry, runtime, definition, replyConfig, embeds);
    }

    /// Returns the underlying [GenericComponentInteractionCreateEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericComponentInteractionCreateEvent], namely [ButtonInteractionEvent],
    ///                                     [EntitySelectInteractionEvent] or [StringSelectInteractionEvent]
    /// @param <T>  a subtype of [GenericComponentInteractionCreateEvent]
    /// @return [T] the event
    public <T extends GenericComponentInteractionCreateEvent> T jdaEvent(Class<T> type) {
        return type.cast(event);
    }

    @Override
    public void deferReply(boolean ephemeral) {
        event.deferReply(ephemeral).complete();
    }

    /// No-op acknowledgement of this interaction.
    ///
    /// This tells discord you intend to update the message that the triggering component is a part of instead of
    /// sending a reply message. You are not required to actually update the message, this will simply acknowledge that
    /// you accepted the interaction.
    ///
    /// **You only have 3 seconds to acknowledge an interaction!**
    ///
    /// When the acknowledgement is sent after the interaction expired, you will receive [ErrorResponse#UNKNOWN_INTERACTION].
    ///
    /// Use [#reply(String, Entry...)] to edit it directly.
    public void deferEdit() {
        event.deferEdit().complete();
    }
}
