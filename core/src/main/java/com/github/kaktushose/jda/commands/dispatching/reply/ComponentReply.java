package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

/// Subtype of [ConfigurableReply] that allows replying only with a name without a message.
public final class ComponentReply extends ConfigurableReply {

    /// Constructs a new ComponentReply.
    ///
    /// @param reply the underlying [ConfigurableReply]
    public ComponentReply(@NotNull ConfigurableReply reply) {
        super(reply);
    }

    /// Sends the reply to Discord and blocks the current thread until the message was sent.
    ///
    /// @return the [Message] that got created
    /// @implNote This method can handle both message replies and message edits. it will check if the interaction got
    /// acknowledged and will acknowledge it if necessary before sending or editing a message. After that,
    /// [InteractionHook#sendMessage(MessageCreateData)] or respectively [InteractionHook#editOriginal(MessageEditData)]
    /// will be called.
    ///
    /// If `keepComponents` is `true`, queries the original message first and adds its components to the reply before sending it.
    public Message reply() {
        return replyAction.reply();
    }

}
