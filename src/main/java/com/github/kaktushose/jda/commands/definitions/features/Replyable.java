package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.GlobalReplyConfig;
import org.jetbrains.annotations.NotNull;

public sealed interface Replyable extends Invokeable permits InteractionDefinition {

    @NotNull
    default ReplyConfig replyConfig() {
        var global = clazz().annotation(com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig.class);
        var local = method().annotation(com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig.class);

        if (global.isEmpty() && local.isEmpty()) {
            return new ReplyConfig();
        }

        return local.map(ReplyConfig::new).orElseGet(() -> new ReplyConfig(global.get()));

    }

    /// Stores the configuration values for sending replies. This acts as a representation of
    /// [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig].
    ///
    /// @see [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig]
    record ReplyConfig(boolean ephemeral, boolean keepComponents, boolean editReply) {

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
}
