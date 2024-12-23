package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.definitions.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.EphemeralInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

/// This class is a subclass of [Event]. It provides additional features for replying to a [GenericComponentInteractionCreateEvent].
///
/// @see Event
/// @see ModalReplyableEvent
/// @since 4.0.0
public final class ComponentEvent extends ModalReplyableEvent<GenericComponentInteractionCreateEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event               the [GenericComponentInteractionCreateEvent] this event holds
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the corresponding [Runtime]
    /// @param definition          the corresponding [EphemeralInteractionDefinition]
    public ComponentEvent(@NotNull GenericComponentInteractionCreateEvent event,
                          @NotNull InteractionRegistry interactionRegistry,
                          @NotNull Runtime runtime,
                          @NotNull EphemeralInteractionDefinition definition) {
        super(event, interactionRegistry, runtime, definition);
    }

    /// Returns the underlying [GenericComponentInteractionCreateEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericComponentInteractionCreateEvent], namely [ButtonInteractionEvent],
    /// [EntitySelectInteractionEvent] or [StringSelectInteractionEvent]
    /// @param <T>  a subtype of [GenericComponentInteractionCreateEvent]
    /// @return [T]
    public <T extends GenericComponentInteractionCreateEvent> T jdaEvent(Class<T> type) {
        return type.cast(event);
    }
}
