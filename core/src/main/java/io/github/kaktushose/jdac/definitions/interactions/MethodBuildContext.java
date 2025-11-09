package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition.CommandConfig;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition.CooldownDefinition;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.message.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/// Holds all objects needed to create an [InteractionDefinition].
@ApiStatus.Internal
public record MethodBuildContext(
        Validators validators,
        LocalizationFunction localizationFunction,
        I18n i18n,
        Interaction interaction,
        Set<String> permissions,
        @Nullable CooldownDefinition cooldownDefinition,
        ClassDescription clazz,
        MethodDescription method,
        Collection<AutoCompleteDefinition> autoCompleteDefinitions,
        CommandConfig globalCommandConfig
) {}
