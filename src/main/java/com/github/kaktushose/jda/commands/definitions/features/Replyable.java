package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
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
}
