package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/// Subtype of [ConfigurableReply] that allows replying only with a name without a message.
public final class ComponentReply extends ConfigurableReply {

    /// Constructs a new ComponentReply.
    ///
    /// @param reply the underlying [ConfigurableReply]
    public ComponentReply(@NotNull ConfigurableReply reply) {
        super(reply);
    }

    /// Sends the reply to Discord.
    public Message reply() {
        return complete();
    }

}
