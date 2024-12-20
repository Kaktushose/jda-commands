package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/// This class is a subclass of [Event]. It provides additional features for replying to a [GenericCommandInteractionEvent].
///
/// @see Event
/// @see ModalReplyableEvent
/// @since 4.0.0
public final class CommandEvent extends ModalReplyableEvent<GenericCommandInteractionEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event               the [GenericCommandInteractionEvent] this event holds
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the corresponding [Runtime]
    /// @param definition          the corresponding [EphemeralInteractionDefinition]
    public CommandEvent(@NotNull GenericCommandInteractionEvent event,
                        @NotNull InteractionRegistry interactionRegistry,
                        @NotNull Runtime runtime,
                        @NotNull EphemeralInteractionDefinition definition) {
        super(event, interactionRegistry, runtime, definition);
    }

    /// Returns the underlying [GenericCommandInteractionEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericCommandInteractionEvent], like [SlashCommandInteractionEvent]
    /// @return [T]
    /// @param <T> a subtype of [GenericCommandInteractionEvent]
    @NotNull
    public <T extends GenericCommandInteractionEvent> T jdaEvent(@NotNull Class<T> type) {
        return type.cast(event);
    }

}
