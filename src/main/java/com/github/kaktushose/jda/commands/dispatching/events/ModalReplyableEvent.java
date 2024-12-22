package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.Modal;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Subtype of [ReplyableEvent] that also supports replying with a [Modal].
///
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see CommandEvent
/// @see ComponentEvent
/// @since 4.0.0
public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent>
        extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    private static final Logger log = LoggerFactory.getLogger(ModalReplyableEvent.class);

    /// Constructs a new ModalReplyableEvent.
    ///
    /// @param event               the subtype [T] of [GenericInteractionCreateEvent]
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the [Runtime] this event lives in
    /// @param definition          the [EphemeralInteractionDefinition] this event belongs to
    protected ModalReplyableEvent(@NotNull T event,
                                  @NotNull InteractionRegistry interactionRegistry,
                                  @NotNull Runtime runtime,
                                  @NotNull EphemeralInteractionDefinition definition) {
        super(event, interactionRegistry, runtime, definition);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target user's Discord client.
    ///
    /// @param modal the method name of the [Modal] you want to reply with
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(@NotNull String modal) {
        if (event instanceof IModalCallback callback) {
            var definitionId = String.valueOf((definition.getMethod().getDeclaringClass().getName() + modal).hashCode());
            var modalDefinition = interactionRegistry.find(ModalDefinition.class, false, it ->
                    it.getDefinitionId().equals(definitionId)
            );
            log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.getDisplayName(), modalDefinition.getDisplayName(), runtimeId());
            callback.replyModal(modalDefinition.toModal(runtimeId())).queue();
        } else {
            throw new IllegalStateException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }
    }
}
