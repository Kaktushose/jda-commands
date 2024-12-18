package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.dispatching.reply.GlobalReplyConfig;
import org.jetbrains.annotations.NotNull;

public record ReplyConfig(boolean ephemeral, boolean keepComponents, boolean editReply) {

    public ReplyConfig() {
        this(GlobalReplyConfig.ephemeral, GlobalReplyConfig.keepComponents, GlobalReplyConfig.editReply);
    }

    public ReplyConfig(@NotNull com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig replyConfig) {
        this(replyConfig.ephemeral(), replyConfig.keepComponents(), replyConfig.editReply());
    }

}
