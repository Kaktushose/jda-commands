package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import org.jetbrains.annotations.NotNull;

public sealed interface Replyable extends Invokeable permits Interaction, ButtonDefinition, EntitySelectMenuDefinition, ModalDefinition, SlashCommandDefinition, StringSelectMenuDefinition {

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
