package com.github.kaktushose.jda.commands.definitions.api.features;

import com.github.kaktushose.jda.commands.definitions.api.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.api.interactions.SelectMenuDefinition;

import static com.github.kaktushose.jda.commands.definitions.api.interactions.SlashCommandDefinition.ParameterDefinition;

public sealed interface JDAEntity<T> permits ButtonDefinition, CommandDefinition, ModalDefinition, SelectMenuDefinition, ParameterDefinition {

    T toJDAEntity();

}
