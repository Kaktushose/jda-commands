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
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

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

    public void replyModal(String modal, ModalTopLevelComponent component, ModalTopLevelComponent... components) {
        replyModal(modal, Stream.concat(Stream.of(component), Arrays.stream(components)).toList());
    }

    public void replyModal(String modal, Collection<ModalTopLevelComponent> components, Entry... placeholders) {
        if (!(jdaEvent() instanceof IModalCallback callback)) {
            throw new InternalException("reply-failed", entry("event", jdaEvent().getClass().getName()));
        }

        InteractionDefinition definition = scopedInvocationContext().definition();
        String definitionId = String.valueOf((definition.classDescription().name() + modal).hashCode());
        ModalDefinition modalDefinition = scopedInteractionRegistry().find(
                ModalDefinition.class,
                false,
                it -> it.definitionId().equals(definitionId)
        );

        ComponentResolver<ModalTopLevelComponent> resolver = new ComponentResolver<>(scopedMessageResolver(), ModalTopLevelComponent.class);
        modalDefinition = modalDefinition.with(
                scopedMessageResolver().resolve(modalDefinition.title(), scopedUserLocale(), Entry.toMap(placeholders)),
                resolver.resolve(components, scopedUserLocale(), Entry.toMap(placeholders))
        );

        log.debug("Replying to interaction \"{}\" with Modal: \"{}\". [Runtime={}]", definition.displayName(), modalDefinition.displayName(), runtimeId());
        callback.replyModal(modalDefinition.toJDAEntity(new CustomId(runtimeId(), definitionId))).queue();
    }
}
