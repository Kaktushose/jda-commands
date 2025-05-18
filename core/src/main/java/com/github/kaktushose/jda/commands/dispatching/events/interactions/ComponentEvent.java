package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

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
    public ComponentEvent(@NotNull GenericComponentInteractionCreateEvent event,
                          @NotNull InteractionRegistry registry,
                          @NotNull Runtime runtime,
                          @NotNull InteractionDefinition definition,
                          @NotNull InteractionDefinition.ReplyConfig replyConfig) {
        super(event, registry, runtime, definition, replyConfig);
    }

    /// Returns the underlying [GenericComponentInteractionCreateEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericComponentInteractionCreateEvent], namely [ButtonInteractionEvent],
    ///                         [EntitySelectInteractionEvent] or [StringSelectInteractionEvent]
    /// @param <T>  a subtype of [GenericComponentInteractionCreateEvent]
    /// @return [T]
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
    /// Use [#reply(String)] to edit it directly.
    public void deferEdit() {
        event.deferEdit().complete();
    }
}
