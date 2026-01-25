package io.github.kaktushose.jdac.dispatching.events.interactions;

import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.ModalReplyableEvent;
import io.github.kaktushose.jdac.dispatching.reply.EditableConfigurableReply;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;

import static io.github.kaktushose.jdac.introspection.internal.IntrospectionAccess.scopedReplyConfig;

/// This class is a subclass of [Event]. It provides additional features for replying to a [GenericComponentInteractionCreateEvent].
///
/// @see Event
/// @see ModalReplyableEvent
public final class ComponentEvent extends ModalReplyableEvent<GenericComponentInteractionCreateEvent> {

    private static final Logger log = JDACLogger.getLogger(ComponentEvent.class);

    /// Returns the underlying [GenericComponentInteractionCreateEvent] and casts it to the given type.
    ///
    /// @param type a subtype of [GenericComponentInteractionCreateEvent], namely [ButtonInteractionEvent],
    ///                                     [EntitySelectInteractionEvent] or [StringSelectInteractionEvent]
    /// @param <T>  a subtype of [GenericComponentInteractionCreateEvent]
    /// @return [T] the event
    public <T extends GenericComponentInteractionCreateEvent> T jdaEvent(Class<T> type) {
        return type.cast(jdaEvent());
    }

    @Override
    public void deferReply(boolean ephemeral) {
        jdaEvent().deferReply(ephemeral).complete();
    }

    /// No-op acknowledgement of this interaction.
    ///
    /// This tells discord you intend to update the message that the triggering component is a part of instead of
    /// sending a reply message. You are not required to actually update the message, this will simply acknowledge that
    /// you accepted the interaction.
    ///
    /// **You only have 3 seconds to acknowledge an interaction!**
    ///
    /// When the acknowledgement is sent after the interaction expired, you will receive [ErrorResponse#UNKNOWN_INTERACTION].
    ///
    /// Use [#reply(String, Entry...)] to edit it directly.
    public void deferEdit() {
        jdaEvent().deferEdit().complete();
    }

    /// Removes all components from the original message.
    ///
    /// The original message is the message, from which this event (interaction) originates. For example if this event is a ButtonEvent, the original message will be the message to which the pressed button is attached to.
    public void removeComponents() {
        log.debug("Reply Debug: Removing components from original message");
        if (jdaEvent().getMessage().isUsingComponentsV2()) {
            throw new UnsupportedOperationException(JDACException.errorMessage("remove-components-v2"));
        }
        if (!jdaEvent().isAcknowledged()) {
            jdaEvent().deferReply(scopedReplyConfig().ephemeral()).complete();
        }
        jdaEvent().getHook().editOriginalComponents().complete();
    }

    @Override
    public EditableConfigurableReply with() {
        return new EditableConfigurableReply(scopedReplyConfig(), jdaEvent());
    }

    /// No-op acknowledgement of this event with the V2 Components of the original reply.
    ///
    /// Calling this method will enforce [EditableConfigurableReply#keepComponents(boolean)] to `true` to retrieve the original components.
    ///
    /// @throws UnsupportedOperationException if the original message didn't use V2 Components
    public Message reply() {
        return with().reply();
    }

    /// Acknowledgement of this event with the V2 Components of the original reply. Will also apply the passed
    /// [ComponentReplacer] before sending the reply.
    ///
    /// Calling this method will enforce [EditableConfigurableReply#keepComponents(boolean)] to `true` to retrieve the original components.
    ///
    /// @param replacer    the [ComponentReplacer] to apply to the original components
    /// @param placeholder the [placeholders][Entry] to use. See [PlaceholderResolver]
    /// @throws UnsupportedOperationException if the original message didn't use V2 Components
    /// @implNote The [ComponentReplacer] only gets applied after the original components were retrieved and, if
    /// [#keepSelections(boolean)] is set to `true`, after the selections are retrieved.
    public Message reply(ComponentReplacer replacer, Entry... placeholder) {
        return with().reply(replacer, placeholder);
    }
}
