package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

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
