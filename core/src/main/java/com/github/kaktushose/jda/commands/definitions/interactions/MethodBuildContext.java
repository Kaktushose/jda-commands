package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition.CommandConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition.CooldownDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/// Holds all objects needed to create an [InteractionDefinition].
public record MethodBuildContext(
        Validators validators,
        LocalizationFunction localizationFunction,
        Interaction interaction,
        Set<String> permissions,
        @Nullable CooldownDefinition cooldownDefinition,
        ClassDescription clazz,
        MethodDescription method,
        Collection<AutoCompleteDefinition> autoCompleteDefinitions,
        CommandConfig globalCommandConfig
        ) {}
