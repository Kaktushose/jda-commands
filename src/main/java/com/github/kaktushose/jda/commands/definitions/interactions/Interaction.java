package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.Invokeable;
import com.github.kaktushose.jda.commands.definitions.features.Permissions;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;
import org.jetbrains.annotations.NotNull;

public sealed interface Interaction extends Definition, Invokeable, Permissions, Replyable
        permits ButtonDefinition, EntitySelectMenuDefinition, ModalDefinition, StringSelectMenuDefinition, CommandDefinition {

    @NotNull
    @Override
    default String definitionId() {
        return String.valueOf((clazz().name() + method().name()).hashCode());
    }
}
