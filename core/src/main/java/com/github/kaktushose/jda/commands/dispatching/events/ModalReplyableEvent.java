package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.Modal;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.ModalBuilder;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static com.github.kaktushose.jda.commands.dispatching.context.internal.RichInvocationContext.getFramework;
import static com.github.kaktushose.jda.commands.dispatching.context.internal.RichInvocationContext.getInvocationContext;
import static com.github.kaktushose.jda.commands.message.placeholder.Entry.entry;

/// Subtype of [ReplyableEvent] that also supports replying with a [Modal].
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see CommandEvent
/// @see ComponentEvent
public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent>
        extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    private static final Logger log = LoggerFactory.getLogger(ModalReplyableEvent.class);

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param modal       the method name of the [Modal] you want to reply with
    /// @param placeholder the [Entry] placeholders to use for localization
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, Entry... placeholder) {
        replyModal(modal, builder -> builder.placeholder(placeholder));
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param modal    the method name of the [Modal] you want to reply with
    /// @param callback a [Function] to dynamically modify the [Modal] before replying with it
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, Function<ModalBuilder, ModalBuilder> callback) {
        InteractionDefinition definition = getInvocationContext().definition();

        if (jdaEvent() instanceof IModalCallback modalCallback) {
            var definitionId = String.valueOf((definition.classDescription().name() + modal).hashCode());
            var modalDefinition = getFramework().interactionRegistry().find(ModalDefinition.class, false, it ->
                    it.definitionId().equals(definitionId)
            );
            var builtModal = callback.apply(new ModalBuilder(this, new CustomId(runtimeId(), definitionId), modalDefinition)).build();

            log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.displayName(), modalDefinition.displayName(), runtimeId());
            modalCallback.replyModal(builtModal).queue();
        } else {
            throw new InternalException("reply-failed", entry("event", jdaEvent().getClass().getName()));
        }
    }
}
