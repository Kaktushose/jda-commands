package com.github.kaktushose.jda.commands.definitions.reflect.interactions;

import com.github.kaktushose.jda.commands.dispatching.reply.GlobalReplyConfig;
import org.jetbrains.annotations.NotNull;

/// Stores the configuration values for sending replies. This acts as a representation of
/// [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig].
///
/// @see [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig]
/// @since 4.0.0
public record ReplyConfig(boolean ephemeral, boolean keepComponents, boolean editReply) {

    /// Constructs a new ReplyConfig using the default values specified by [GlobalReplyConfig].
    public ReplyConfig() {
        this(GlobalReplyConfig.ephemeral, GlobalReplyConfig.keepComponents, GlobalReplyConfig.editReply);
    }

    /// Constructs a new ReplyConfig.
    ///
    /// @param replyConfig the [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig] to represent
    public ReplyConfig(@NotNull com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig replyConfig) {
        this(replyConfig.ephemeral(), replyConfig.keepComponents(), replyConfig.editReply());
    }
}
