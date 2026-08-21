package io.github.kaktushose.jdac.dispatching.events;

import io.github.kaktushose.jdac.annotations.interactions.Modal;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.reply.ModalReply;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/// Subtype of [ReplyableEvent] that also supports replying with a [Modal].
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see CommandEvent
/// @see ComponentEvent
public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent>
        extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param modal        the method name of the [Modal] you want to reply with
    /// @param component    the [ModalTopLevelComponent] to add to this modal
    /// @param placeholders the [Entry] placeholders to use for [message resolution][MessageResolver]
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, ModalTopLevelComponent component, Entry... placeholders) {
        reply(null, modal, List.of(component), placeholders);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param origin       the [Class] the modal handler is defined in
    /// @param modal        the method name of the [Modal] you want to reply with
    /// @param component    the [ModalTopLevelComponent] to add to this modal
    /// @param placeholders the [Entry] placeholders to use for [message resolution][MessageResolver]
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(Class<?> origin, String modal, ModalTopLevelComponent component, Entry... placeholders) {
        reply(origin, modal, List.of(component), placeholders);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param modal        the method name of the [Modal] you want to reply with
    /// @param components   a [Collection] of [ModalTopLevelComponent]s to add to this modal
    /// @param placeholders the [Entry] placeholders to use for [message resolution][MessageResolver]
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(String modal, Collection<ModalTopLevelComponent> components, Entry... placeholders) {
        reply(null, modal, components, placeholders);
    }

    /// Acknowledgement of this event with a [Modal]. This will open a popup on the target users Discord client.
    ///
    /// @param origin       the [Class] the modal handler is defined in
    /// @param modal        the method name of the [Modal] you want to reply with
    /// @param components   a [Collection] of [ModalTopLevelComponent]s to add to this modal
    /// @param placeholders the [Entry] placeholders to use for [message resolution][MessageResolver]
    /// @throws IllegalArgumentException if no [Modal] with the given name was found
    public void replyModal(Class<?> origin, String modal, Collection<ModalTopLevelComponent> components, Entry... placeholders) {
        reply(origin, modal, components, placeholders);
    }

    private void reply(@Nullable Class<?> origin, String modal, Collection<ModalTopLevelComponent> components, Entry... placeholders) {
        new ModalReply().reply(origin, modal, components, placeholders);
    }
}
