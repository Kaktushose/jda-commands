package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.Definition;
import com.github.kaktushose.jda.commands.definitions.api.features.Invokeable;

import java.util.Set;

public non-sealed interface AutoCompleteDefinition extends Invokeable, Definition {

    Set<String> commands();

}
