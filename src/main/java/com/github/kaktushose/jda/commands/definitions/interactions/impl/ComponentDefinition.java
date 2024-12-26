package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.SelectMenuDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public sealed interface ComponentDefinition<T> extends InteractionDefinition, JDAEntity<T>, CustomIdJDAEntity<T>
        permits ButtonDefinition, SelectMenuDefinition {

    @NotNull ClassDescription clazz();

    @NotNull MethodDescription method();

    @NotNull Collection<String> permissions();

}
