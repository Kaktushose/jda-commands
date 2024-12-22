package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.internal.Helpers;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SelectOption;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a {@link net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu StringSelectMenu}.
 *
 * @see StringSelectMenu
 * @since 4.0.0
 */
public final class StringSelectMenuDefinition extends GenericSelectMenuDefinition<net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu> {

    private final Set<SelectOptionDefinition> selectOptions;

    private StringSelectMenuDefinition(Method method,
                                       Set<String> permissions,
                                       ReplyConfig replyConfig,
                                       Set<SelectOptionDefinition> selectOptions,
                                       String placeholder,
                                       int minValue,
                                       int maxValue) {
        super(method, permissions, replyConfig, placeholder, minValue, maxValue);
        this.selectOptions = selectOptions;
    }

    /**
     * Builds a new StringSelectMenuDefinition.
     *
     * @return an {@link Optional} holding the StringSelectMenuDefinition
     */
    public static Optional<StringSelectMenuDefinition> build(MethodBuildContext context) {
        Method method = context.method();
        if (!method.isAnnotationPresent(StringSelectMenu.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterAmount(method, 2)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterType(method, 0, ComponentEvent.class) ||
                Helpers.isIncorrectParameterType(method, 1, List.class)) {
            return Optional.empty();
        }

        StringSelectMenu selectMenu = method.getAnnotation(StringSelectMenu.class);

        Set<SelectOptionDefinition> selectOptions = new HashSet<>();
        for (SelectOption option : method.getDeclaredAnnotationsByType(SelectOption.class)) {
            selectOptions.add(SelectOptionDefinition.build(option));
        }

        return Optional.of(new StringSelectMenuDefinition(
                method,
                Helpers.permissions(context),
                Helpers.replyConfig(method),
                selectOptions,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    public net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu toSelectMenu(String customId, boolean enabled) {
        return net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.create(customId)
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
                ", replyConfig=" + replyConfig +
                ", permissions=" + permissions +
                ", id='" + definitionId + '\'' +
                ", method=" + method +
                '}';
    }
}
