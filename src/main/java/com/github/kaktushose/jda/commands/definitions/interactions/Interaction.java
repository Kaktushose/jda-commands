package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.Invokeable;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.AutoCompleteDefinition;

public sealed interface Interaction extends Definition, Invokeable
        permits CustomIdInteraction, PermissionsInteraction, AutoCompleteDefinition {

    @Override
    default String definitionId() {
        return String.valueOf((method().getDeclaringClass().getName() + method().getName()).hashCode());
    }
}
