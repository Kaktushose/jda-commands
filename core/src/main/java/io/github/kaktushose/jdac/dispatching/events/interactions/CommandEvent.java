package io.github.kaktushose.jdac.dispatching.events.interactions;

import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.ModalReplyableEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/// This class is a subclass of [Event]. It provides additional features for replying to a
///  [GenericCommandInteractionEvent].
///
/// @see Event
/// @see ModalReplyableEvent
public final class CommandEvent extends ModalReplyableEvent<GenericCommandInteractionEvent> {

    /// Returns the underlying [GenericCommandInteractionEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericCommandInteractionEvent], like [SlashCommandInteractionEvent]
    /// @param <T>  a subtype of [GenericCommandInteractionEvent]
    /// @return [T] the event
    public <T extends GenericCommandInteractionEvent> T jdaEvent(Class<T> type) {
        return type.cast(jdaEvent());
    }

    @Override
    public void deferReply(boolean ephemeral) {
        jdaEvent().deferReply(ephemeral).complete();
    }
}
