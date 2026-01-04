package io.github.kaktushose.jdac.dispatching.events;

import io.github.kaktushose.jdac.annotations.interactions.Modal;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;

import static io.github.kaktushose.jdac.introspection.internal.IntrospectionAccess.*;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Subtype of [ReplyableEvent] that also supports replying with a [Modal].
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see CommandEvent
/// @see ComponentEvent
public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent>
        extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    private static final Logger log = JDACLogger.getLogger(ModalReplyableEvent.class);


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
        if (!(jdaEvent() instanceof IModalCallback callback)) {
            throw new InternalException("reply-failed", entry("event", jdaEvent().getClass().getName()));
        }
        Checks.notEmpty(components, "Modal components");

        InteractionDefinition definition = scopedInvocationContext().definition();
        String className = origin == null ? definition.classDescription().name() : origin.getName();
        String definitionId = InteractionDefinition.createDefinitionId(className, modal);
        ModalDefinition modalDefinition = scopedInteractionRegistry().find(
                ModalDefinition.class,
                false,
                it -> it.definitionId().equals(definitionId)
        );

        var entryMap = Entry.toMap(placeholders);
        ComponentResolver<ModalTopLevelComponent> resolver = new ComponentResolver<>(scopedMessageResolver(), ModalTopLevelComponent.class);
        modalDefinition = modalDefinition.with(
                scopedMessageResolver().resolve(modalDefinition.title(), scopedUserLocale(), entryMap),
                resolver.resolve(components, scopedUserLocale(), entryMap)
        );

        log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.displayName(), modalDefinition.displayName(), runtimeId());
        callback.replyModal(modalDefinition.toJDAEntity(new CustomId(runtimeId(), definitionId))).queue();
    }
}
