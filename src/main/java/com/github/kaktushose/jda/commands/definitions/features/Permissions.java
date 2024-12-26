package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public sealed interface Permissions extends Invokeable permits InteractionDefinition {

    @NotNull
    Collection<String> permissions();

}
