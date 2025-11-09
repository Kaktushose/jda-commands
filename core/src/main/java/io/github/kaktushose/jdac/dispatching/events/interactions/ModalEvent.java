package io.github.kaktushose.jdac.dispatching.events.interactions;

import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.ReplyableEvent;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;

/// This class is a subclass of [Event]. It provides additional features for replying to a [ModalInteractionEvent].
///
/// @see Event
/// @see ReplyableEvent
public final class ModalEvent extends ReplyableEvent<ModalInteractionEvent> {

    @Override
    public void deferReply(boolean ephemeral) {
        jdaEvent().deferReply(ephemeral).complete();
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
        jdaEvent().deferEdit().complete();
    }
}
