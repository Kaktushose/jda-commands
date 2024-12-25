package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import org.jetbrains.annotations.NotNull;

public sealed interface JDAEntity<T> permits ParameterDefinition, ButtonDefinition, ContextCommandDefinition, EntitySelectMenuDefinition, ModalDefinition, ModalDefinition.TextInputDefinition, SlashCommandDefinition, StringSelectMenuDefinition, StringSelectMenuDefinition.SelectOptionDefinition {

    @NotNull T toJDAEntity();

    default T toJDAEntity(@NotNull String runtimeId) {
        return toJDAEntity();
    }

}
