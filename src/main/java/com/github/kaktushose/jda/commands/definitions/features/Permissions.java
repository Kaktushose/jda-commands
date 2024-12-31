package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public sealed interface Permissions extends Invokable permits InteractionDefinition {

    @NotNull
    Collection<String> permissions();

}
