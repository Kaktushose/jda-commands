package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.StringSelectMenuDefinition;
import org.jetbrains.annotations.NotNull;

public sealed interface CustomIdJDAEntity<T> permits ModalDefinition, EntitySelectMenuDefinition, StringSelectMenuDefinition {

    @NotNull
    T toJDAEntity(@NotNull CustomId customId);

}
