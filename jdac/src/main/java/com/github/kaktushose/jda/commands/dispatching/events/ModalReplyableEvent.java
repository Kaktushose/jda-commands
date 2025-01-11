package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.Modal;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
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
public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent>
        extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    private static final Logger log = LoggerFactory.getLogger(ModalReplyableEvent.class);

    /// Constructs a new ModalReplyableEvent.
    ///
    /// @param event      the subtype [T] of [GenericInteractionCreateEvent]
    /// @param registry   the corresponding [InteractionRegistry]
    /// @param runtime    the [Runtime] this event lives in
    /// @param definition the [InteractionDefinition] this event belongs to
    protected ModalReplyableEvent(@NotNull T event,
                                  @NotNull InteractionRegistry registry,
                                  @NotNull Runtime runtime,
                                  @NotNull InteractionDefinition definition,
                                  @NotNull InteractionDefinition.ReplyConfig global
                                  ) {
        super(event, registry, runtime, definition, global);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target user's Discord client.
    ///
    /// @param modal the method name of the [Modal] you want to reply with
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(@NotNull String modal) {
        if (event instanceof IModalCallback callback) {
            var definitionId = String.valueOf((definition.clazzDescription().name() + modal).hashCode());
            var modalDefinition = registry.find(ModalDefinition.class, false, it ->
                    it.definitionId().equals(definitionId)
            );
            log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.displayName(), modalDefinition.displayName(), runtimeId());
            callback.replyModal(modalDefinition.toJDAEntity(new CustomId(runtimeId(), definitionId))).queue();
        } else {
            throw new IllegalStateException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }
    }
}
