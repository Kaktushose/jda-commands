package io.github.kaktushose.jdac.dispatching.events.interactions;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.ModalReplyableEvent;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/// This class is a subclass of [Event]. It provides additional features for replying to a [GenericCommandInteractionEvent].
///
/// @see Event
/// @see ModalReplyableEvent
public final class CommandEvent extends ModalReplyableEvent<GenericCommandInteractionEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event       the [GenericCommandInteractionEvent] this event holds
    /// @param registry    the corresponding [InteractionRegistry]
    /// @param runtime     the corresponding [Runtime]
    /// @param definition  the corresponding [InteractionDefinition]
    /// @param replyConfig the [InteractionDefinition.ReplyConfig] to use
    /// @param embeds      the corresponding [Embeds]
    public CommandEvent(GenericCommandInteractionEvent event,
                        InteractionRegistry registry,
                        Runtime runtime,
                        InteractionDefinition definition,
                        InteractionDefinition.ReplyConfig replyConfig,
                        Embeds embeds) {
        super(event, registry, runtime, definition, replyConfig, embeds);
    }

    /// Returns the underlying [GenericCommandInteractionEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericCommandInteractionEvent], like [SlashCommandInteractionEvent]
    /// @param <T>  a subtype of [GenericCommandInteractionEvent]
    /// @return [T] the event
    public <T extends GenericCommandInteractionEvent> T jdaEvent(Class<T> type) {
        return type.cast(event);
    }

    @Override
    public void deferReply(boolean ephemeral) {
        event.deferReply(ephemeral).complete();
    }
}
