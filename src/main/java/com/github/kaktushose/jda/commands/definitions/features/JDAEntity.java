package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition.TextInputDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition.SelectOptionDefinition;
import org.jetbrains.annotations.NotNull;

public sealed interface JDAEntity<T> extends Definition
        permits ComponentDefinition, TextInputDefinition, CommandDefinition, ParameterDefinition, SelectOptionDefinition {

    @NotNull
    T toJDAEntity();

}
