package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.internal.Helpers;
import org.jetbrains.annotations.NotNull;

public sealed interface Replyable extends Invokeable
        permits ButtonDefinition, ContextCommandDefinition, EntitySelectMenuDefinition, ModalDefinition, SlashCommandDefinition, StringSelectMenuDefinition {

    @NotNull
    default ReplyConfig replyConfig() {
        return Helpers.replyConfig(method());
    }

}
