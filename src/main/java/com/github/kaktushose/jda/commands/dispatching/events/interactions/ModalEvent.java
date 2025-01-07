package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/// This class is a subclass of [Event]. It provides additional features for replying to a [ModalInteractionEvent].
///
/// @see Event
/// @see ReplyableEvent
public final class ModalEvent extends ReplyableEvent<ModalInteractionEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event      the [GenericCommandInteractionEvent] this event holds
    /// @param registry   the corresponding [InteractionRegistry]
    /// @param runtime    the corresponding [Runtime]
    /// @param definition the corresponding [InteractionDefinition]
    public ModalEvent(@NotNull ModalInteractionEvent event,
                      @NotNull InteractionRegistry registry,
                      @NotNull Runtime runtime,
                      @NotNull InteractionDefinition definition,
                      @NotNull InteractionDefinition.ReplyConfig global) {
        super(event, registry, runtime, definition, global);
    }

}
