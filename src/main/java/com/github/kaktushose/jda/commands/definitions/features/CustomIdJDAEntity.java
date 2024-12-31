package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import org.jetbrains.annotations.NotNull;

public sealed interface CustomIdJDAEntity<T> extends Definition permits ComponentDefinition, ModalDefinition {

    @NotNull
    T toJDAEntity(@NotNull CustomId customId);

}
