package com.github.kaktushose.jda.commands.definitions;

import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import org.jetbrains.annotations.NotNull;

/// The common interface for all interaction definitions and their sub parts, such as parameters or text inputs, etc.
public sealed interface Definition permits CustomIdJDAEntity, Invokable, JDAEntity, InteractionDefinition,
        ModalDefinition.TextInputDefinition, ParameterDefinition, ParameterDefinition.ConstraintDefinition,
        SlashCommandDefinition.CooldownDefinition, StringSelectMenuDefinition.SelectOptionDefinition {

    /// The id for this definition. Per default this is the hash code of the [#toString()] method.
    @NotNull
    default String definitionId() {
        return String.valueOf(toString().hashCode());
    }

    /// The human-readable name of this definition.
    @NotNull
    String displayName();
}
