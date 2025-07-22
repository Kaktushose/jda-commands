package com.github.kaktushose.jda.commands.dispatching.reply.internal;

import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

/// Common interface for classes that support simple message replies to [GenericInteractionCreateEvent].
///
/// This interface ensures that [ReplyableEvent] and [ReplyAction], which is internally used by [ReplyableEvent],
/// always share the same reply methods.
///
/// @see ReplyableEvent
@ApiStatus.Internal
public sealed interface Reply permits ReplyableEvent, ReplyAction {

    /// Acknowledgement of this event with a text message.
    ///
    /// @param message     the message to send or the localization key
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale , String, I18n.Entry...) ]
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    Message reply(String message, I18n.Entry... placeholder);

    /// Acknowledgement of this event with a [MessageEmbed].
    ///
    /// @param first      the [MessageEmbed] to send
    /// @param additional additional [MessageEmbed]s to send
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    Message reply(MessageEmbed first, MessageEmbed... additional);

    /// Acknowledgement of this event with a [MessageCreateData].
    ///
    /// @param message the [MessageCreateData] to send
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    Message reply(MessageCreateData message);

}
