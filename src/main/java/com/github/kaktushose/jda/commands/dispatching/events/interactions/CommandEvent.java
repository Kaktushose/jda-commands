package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.definitions.Registry;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
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
    /// @param registry the corresponding [Registry]
    /// @param runtime             the corresponding [Runtime]
    /// @param definition          the corresponding [InteractionDefinition]
    public CommandEvent(@NotNull GenericCommandInteractionEvent event,
                        @NotNull Registry registry,
                        @NotNull Runtime runtime,
                        @NotNull InteractionDefinition definition) {
        super(event, registry, runtime, definition);
    }

    /// Returns the underlying [GenericCommandInteractionEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericCommandInteractionEvent], like [SlashCommandInteractionEvent]
    /// @param <T>  a subtype of [GenericCommandInteractionEvent]
    /// @return [T]
    @NotNull
    public <T extends GenericCommandInteractionEvent> T jdaEvent(@NotNull Class<T> type) {
        return type.cast(event);
    }

}
