package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.embeds.Embeds;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

/// This class is a subclass of [Event]. It provides additional features for replying to a [GenericComponentInteractionCreateEvent].
///
/// @see Event
/// @see ModalReplyableEvent
public final class ComponentEvent extends ModalReplyableEvent<GenericComponentInteractionCreateEvent> {

    /// Constructs a new CommandEvent.
    ///
    /// @param event      the [GenericComponentInteractionCreateEvent] this event holds
    /// @param registry   the corresponding [InteractionRegistry]
    /// @param runtime    the corresponding [Runtime]
    /// @param definition the corresponding [InteractionDefinition]
    /// @param embeds     the corresponding [Embeds]
    public ComponentEvent(@NotNull GenericComponentInteractionCreateEvent event,
                          @NotNull InteractionRegistry registry,
                          @NotNull Runtime runtime,
                          @NotNull InteractionDefinition definition,
                          @NotNull InteractionDefinition.ReplyConfig global,
                          @NotNull Embeds embeds) {
        super(event, registry, runtime, definition, global, embeds);
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
}
