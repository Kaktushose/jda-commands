package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.SlashCommandDefinition.ParameterDefinition;

public sealed interface JDAEntity<T> permits ButtonDefinition, CommandDefinition, ModalDefinition, SelectMenuDefinition, SlashCommandDefinition, ParameterDefinition {

    T toJDAEntity();

}
