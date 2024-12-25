package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
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
        Method method,
        Collection<AutoCompleteDefinition> autoCompleteDefinitions
) {
}
