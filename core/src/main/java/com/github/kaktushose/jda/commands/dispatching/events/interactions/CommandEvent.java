package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
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
    /// @param embeds     the corresponding [Embeds]
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
    /// @return [T]
    public <T extends GenericCommandInteractionEvent> T jdaEvent(Class<T> type) {
        return type.cast(event);
    }

    @Override
    public void deferReply(boolean ephemeral) {
        event.deferReply(ephemeral).complete();
    }
}
