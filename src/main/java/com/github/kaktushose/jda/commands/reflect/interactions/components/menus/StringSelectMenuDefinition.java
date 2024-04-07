package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.SelectOption;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu StringSelectMenu}.
 *
 * @see StringSelectMenu
 * @since 4.0.0
 */
public class StringSelectMenuDefinition extends GenericSelectMenuDefinition<net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu> {

    private final Set<SelectOptionDefinition> selectOptions;

    protected StringSelectMenuDefinition(Method method,
                                         Set<String> permissions,
                                         boolean ephemeral,
                                         Set<SelectOptionDefinition> selectOptions,
                                         String placeholder,
                                         int minValue,
                                         int maxValue) {
        super(method, permissions, ephemeral, placeholder, minValue, maxValue);
        this.selectOptions = selectOptions;
    }

    /**
     * Builds a new StringSelectMenuDefinition.
     *
     * @param method the {@link Method} of the button
     * @return an {@link Optional} holding the StringSelectMenuDefinition
     */
    public static Optional<StringSelectMenuDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(StringSelectMenu.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 2) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!ComponentEvent.class.isAssignableFrom(method.getParameters()[0].getType()) &&
                !List.class.isAssignableFrom(method.getParameters()[1].getType())) {
            log.error("An error has occurred! Skipping Button {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s, second parameter of type %s!",
                            ComponentEvent.class.getSimpleName(),
                            List.class.getSimpleName()
                    )));
            return Optional.empty();
        }

        Set<String> permissions = new HashSet<>();
        if (method.isAnnotationPresent(Permissions.class)) {
            Permissions permission = method.getAnnotation(Permissions.class);
            permissions = new HashSet<>(Arrays.asList(permission.value()));
        }

        StringSelectMenu selectMenu = method.getAnnotation(StringSelectMenu.class);

        Set<SelectOptionDefinition> selectOptions = new HashSet<>();
        for (SelectOption option : method.getDeclaredAnnotationsByType(SelectOption.class)) {
            selectOptions.add(SelectOptionDefinition.build(option));
        }

        return Optional.of(new StringSelectMenuDefinition(
                method,
                permissions,
                selectMenu.ephemeral(),
                selectOptions,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    public net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu toSelectMenu(String runtimeId, boolean enabled) {
        return net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.create(createCustomId(runtimeId))
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue)
                .addOptions(selectOptions.stream().map(SelectOptionDefinition::toSelectOption).collect(Collectors.toSet()))
                .setDefaultOptions(selectOptions.stream()
                        .filter(SelectOptionDefinition::isDefault)
                        .map(SelectOptionDefinition::toSelectOption)
                        .collect(Collectors.toSet())
                )
                .setDisabled(!enabled)
                .build();
    }

    /**
     * Gets a set of all {@link SelectOptionDefinition SelectOptionDefinitions}.
     *
     * @return a set of all {@link SelectOptionDefinition SelectOptionDefinitions}
     */
    public Set<SelectOptionDefinition> getSelectOptions() {
        return selectOptions;
    }

    @Override
    public String toString() {
        return "StringSelectMenuDefinition{" +
               "selectOptions=" + selectOptions +
               ", placeholder='" + placeholder + '\'' +
               ", minValue=" + minValue +
               ", maxValue=" + maxValue +
               ", ephemeral=" + ephemeral +
               ", permissions=" + permissions +
               ", id='" + definitionId + '\'' +
               ", method=" + method +
               '}';
    }
}
