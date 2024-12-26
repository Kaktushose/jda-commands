package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ModalDefinition.TextInputDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.StringSelectMenuDefinition.SelectOptionDefinition;
import org.jetbrains.annotations.NotNull;

public sealed interface JDAEntity<T> extends Definition
        permits ParameterDefinition, ComponentDefinition, TextInputDefinition, CommandDefinition, SelectOptionDefinition {

    @NotNull
    T toJDAEntity();

}
