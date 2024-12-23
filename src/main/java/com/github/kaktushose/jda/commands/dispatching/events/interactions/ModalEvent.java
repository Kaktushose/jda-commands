package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.definitions.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.EphemeralInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/// This class is a subclass of [Event]. It provides additional features for replying to a [ModalInteractionEvent].
///
/// @see Event
/// @see ReplyableEvent
/// @since 4.0.0
public final class ModalEvent extends ReplyableEvent<ModalInteractionEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event               the [GenericCommandInteractionEvent] this event holds
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the corresponding [Runtime]
    /// @param definition          the corresponding [EphemeralInteractionDefinition]
    public ModalEvent(@NotNull ModalInteractionEvent event,
                      @NotNull InteractionRegistry interactionRegistry,
                      @NotNull Runtime runtime,
                      @NotNull EphemeralInteractionDefinition definition) {
        super(event, interactionRegistry, runtime, definition);
    }

}
