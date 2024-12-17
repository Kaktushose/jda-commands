package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.data.EmbedDTO;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

public sealed interface Reply permits MessageReply, ReplyableEvent {

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the message to send
     */
    void reply(@NotNull String message);

    /**
     * Sends a formatted message using the specified format string and arguments to the TextChannel where the interaction was executed.
     *
     * @param format the message to send
     * @param args   Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *               format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *               zero.
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
     *                                          is incompatible with the given arguments, insufficient arguments given
     *                                          the format string, or other illegal conditions.
     */
    default void reply(@NotNull String format, @NotNull Object... args) {
        reply(format.formatted(args));
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the {@code Message} to send
     */
    void reply(@NotNull MessageCreateData message);

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    void reply(@NotNull EmbedBuilder builder);

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    default void reply(@NotNull EmbedDTO embedDTO) {
        reply(embedDTO.toEmbedBuilder());
    }
}
