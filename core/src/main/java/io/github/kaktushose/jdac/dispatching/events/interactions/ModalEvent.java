package io.github.kaktushose.jdac.dispatching.events.interactions;

import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.events.ReplyableEvent;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.Objects;

/// This class is a subclass of [Event]. It provides additional features for replying to a [ModalInteractionEvent] as
/// well as for retrieving the [ModalMapping]s.
///
/// @see Event
/// @see ReplyableEvent
public final class ModalEvent extends ReplyableEvent<ModalInteractionEvent> {

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

    /// Returns a List of [ModalMappings][ModalMapping] representing the values
    /// input by the user for each field when the modal was submitted.
    ///
    /// @return Immutable List of [ModalMappings][ModalMapping]
    /// @see #value(String)
    public List<ModalMapping> values() {
        return jdaEvent().getValues();
    }

    /// Convenience method to get a [ModalMapping][ModalMapping] by its id
    /// from the List of [ModalMappings][ModalMapping].
    ///
    /// @param customId The custom id
    /// @return [ModalMapping] with this id, or null if not found
    /// @throws NullPointerException     If no value was found for the provided id
    /// @throws IllegalArgumentException If the provided id is null
    /// @see #values()
    public ModalMapping value(String customId) {
        return Objects.requireNonNull(jdaEvent().getValue(customId), "No value present for custom id '%s'".formatted(customId));
    }

    /// Convenience method to get a [ModalMapping][ModalMapping] by its numeric
    /// id from the List of [ModalMappings][ModalMapping]
    ///
    /// @param uniqueId The uniqueId id
    /// @return [ModalMapping] with this numeric id, or null if not found
    /// @throws NullPointerException     If no value was found for the provided id
    /// @throws IllegalArgumentException If the provided id is null
    /// @see #values()
    public ModalMapping value(int uniqueId) {
        return Objects.requireNonNull(jdaEvent().getValueByUniqueId(uniqueId), "No value present for unique id '%d'".formatted(uniqueId));
    }

    /// Convenience method to get the value of a [TextInput] and convert it to the given type [T] via
    /// [Proteus].
    ///
    /// @param customId The custom id of the [TextInput]
    /// @param type the [Class] of the type to convert to
    /// @return the [ConversionResult]
    /// @param <T> the type to convert to
    public <T> ConversionResult<T> value(String customId, Class<T> type) {
        return Proteus.global().convert(value(customId).getAsString(), Type.of(String.class), Type.of(type));
    }

    /// Convenience method to get the value of a [TextInput] and convert it to the given type [T] via
    /// [Proteus].
    ///
    /// @param uniqueId The unique id of the [TextInput]
    /// @param type the [Class] of the type to convert to
    /// @return the [ConversionResult]
    /// @param <T> the type to convert to
    public <T> ConversionResult<T> value(int uniqueId, Class<T> type) {
        return Proteus.global().convert(value(uniqueId).getAsString(), Type.of(String.class), Type.of(type));
    }
}
