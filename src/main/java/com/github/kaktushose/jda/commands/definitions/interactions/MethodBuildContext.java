package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition.CooldownDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

/// Holds all objects needed to create a [GenericInteractionDefinition].
@ApiStatus.Internal
public record MethodBuildContext(
        @NotNull ValidatorRegistry validatorRegistry,
        @NotNull LocalizationFunction localizationFunction,
        Interaction interaction,
        Set<String> permissions,
        CooldownDefinition cooldownDefinition,
        ClassDescription clazz,
        MethodDescription method,
        Collection<AutoCompleteDefinition> autoCompleteDefinitions
) {}
