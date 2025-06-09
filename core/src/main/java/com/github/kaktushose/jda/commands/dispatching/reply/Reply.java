package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

/// Common interface for classes that support simple message replies to [GenericInteractionCreateEvent].
///
/// This interface ensures that [ReplyableEvent] and [MessageReply], which is internally used by [ReplyableEvent],
/// always share the same reply methods.
///
/// @see ReplyableEvent
public sealed interface Reply permits MessageReply, ReplyableEvent {

    /// Acknowledgement of this event with a text message.
    ///
    /// @param message the message to send
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    Message reply(@NotNull String message, I18n.Entry... placeholder);

    /// Acknowledgement of this event with a text message.
    ///
    /// @param format the message to send
    /// @param args   Arguments referenced by the format specifiers in the format string. If there are more arguments than
    ///               format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
    ///               zero.
    /// @return the [Message] that got created
    /// @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
    ///                                         is incompatible with the given arguments, insufficient arguments given
    ///                                         the format string, or other illegal conditions
    ///                                                                                                                                                                                                                                                                                                is incompatible with the given arguments, insufficient arguments given
    ///                                                                                                                                                                                                                                                                                               the format string, or other illegal conditions.
    default Message reply(@NotNull String format, @NotNull Object... args) {
        return reply(format.formatted(args));
    }

    /// Acknowledgement of this event with a text message.
    ///
    /// @param builder the [EmbedBuilder] to send
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    Message reply(@NotNull EmbedBuilder builder);

    /// Acknowledgement of this event with a text message.
    ///
    /// @param embedDTO the [EmbedDTO] to send
    /// @return the [Message] that got created
    /// @implSpec Internally this method must call [RestAction#complete()], thus the [Message] object can get
    /// returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    default Message reply(@NotNull EmbedDTO embedDTO) {
        return reply(embedDTO.toEmbedBuilder());
    }
}
