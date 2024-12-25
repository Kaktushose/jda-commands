package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.StringSelectMenuDefinition;
import org.jetbrains.annotations.NotNull;

public sealed interface JDAEntity<T>
        permits ParameterDefinition, ButtonDefinition, EntitySelectMenuDefinition, ModalDefinition, ModalDefinition.TextInputDefinition, StringSelectMenuDefinition, StringSelectMenuDefinition.SelectOptionDefinition, CommandDefinition, SlashCommandDefinition {

    @NotNull
    T toJDAEntity();

}
