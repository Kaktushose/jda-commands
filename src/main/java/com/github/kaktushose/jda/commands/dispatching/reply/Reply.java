package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.data.EmbedDTO;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

/// Common interface for classes that support simple message replies to [GenericInteractionCreateEvent].
///
/// This interface ensures that [ReplyableEvent] and [MessageReply], which is internally used by [ReplyableEvent],
/// always share the same reply methods.
///
/// @see ReplyableEvent
/// @since 4.0.0
public sealed interface Reply permits MessageReply, ReplyableEvent {

    /// Acknowledgement of this event with a text message.
    ///
    /// Internally this method will call [RestAction#complete()], thus the [Message] object gets returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    ///
    /// @param message the message to send
    /// @return the [Message] that got created
    Message reply(@NotNull String message);

    /// Acknowledgement of this event with a text message.
    ///
    /// Internally this method will call [RestAction#complete()], thus the [Message] object gets returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
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
    /// Internally this method will call [RestAction#complete()], thus the [Message] object gets returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    ///
    /// @param message the [MessageCreateData] to send
    /// @return the [Message] that got created
    Message reply(@NotNull MessageCreateData message);

    /// Acknowledgement of this event with a text message.
    ///
    /// Internally this method will call [RestAction#complete()], thus the [Message] object gets returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    ///
    /// @param builder the [EmbedBuilder] to send
    /// @return the [Message] that got created
    Message reply(@NotNull EmbedBuilder builder);

    /// Acknowledgement of this event with a text message.
    ///
    /// Internally this method will call [RestAction#complete()], thus the [Message] object gets returned directly.
    ///
    /// This might throw [RuntimeException]s if JDA fails to send the message.
    ///
    /// @param embedDTO the [EmbedDTO] to send
    /// @return the [Message] that got created
    default Message reply(@NotNull EmbedDTO embedDTO) {
        return reply(embedDTO.toEmbedBuilder());
    }
}
