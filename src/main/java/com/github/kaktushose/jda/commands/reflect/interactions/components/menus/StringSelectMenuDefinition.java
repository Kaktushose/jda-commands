package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.SelectOptionEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;
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
    private final String selectOptionProvider;

    protected StringSelectMenuDefinition(Method method,
                                         Set<String> permissions,
                                         boolean ephemeral,
                                         Set<SelectOptionDefinition> selectOptions,
                                         String selectOptionProvider, String placeholder,
                                         int minValue,
                                         int maxValue) {
        super(method, permissions, ephemeral, placeholder, minValue, maxValue);
        this.selectOptions = selectOptions;
        this.selectOptionProvider = selectOptionProvider;
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
            log.error("An error has occurred! Skipping Select Menu {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!ComponentEvent.class.isAssignableFrom(method.getParameters()[0].getType()) &&
            !List.class.isAssignableFrom(method.getParameters()[1].getType())) {
            log.error("An error has occurred! Skipping Select Menu {}.{}:",
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

        String selectOptionProvider = null;
        if (selectOptions.isEmpty() && method.isAnnotationPresent(DynamicOptions.class)) {
            DynamicOptions dynamicOptions = method.getAnnotation(DynamicOptions.class);
            if (!dynamicOptions.value().isBlank()) {
                selectOptionProvider = dynamicOptions.value();
            }
        }

        if (selectOptions.isEmpty() && selectOptionProvider == null) {
            log.error("An error has occurred! Skipping Select Menu {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Cannot build select menu without options! Either use SelectOption or DynamicOptions"));
            return Optional.empty();
        }

        return Optional.of(new StringSelectMenuDefinition(
                method,
                permissions,
                selectMenu.ephemeral(),
                selectOptions,
                selectOptionProvider,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    public net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu toSelectMenu(RuntimeSupervisor.InteractionRuntime runtime, boolean enabled, InteractionRegistry interactionRegistry) {
        var menu = net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.create(createCustomId(runtime.getRuntimeId()))
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue)
                .setDefaultOptions(selectOptions.stream()
                        .filter(SelectOptionDefinition::isDefault)
                        .map(SelectOptionDefinition::toSelectOption)
                        .collect(Collectors.toSet())
                )
                .setDisabled(!enabled);

        menu.addOptions(selectOptions.stream().map(SelectOptionDefinition::toSelectOption).collect(Collectors.toSet()));

        if (selectOptionProvider != null) {
            Optional<SelectOptionProviderDefinition> optionalProvider = interactionRegistry
                    .getSelectOptionProviders()
                    .stream()
                    .filter(it -> String.format("%s%s", it.getMethod().getDeclaringClass().getSimpleName(), selectOptionProvider).equals(it.getDefinitionId()))
                    .findFirst();

            if (optionalProvider.isEmpty()) {
                log.warn("Select option provider {} not found!", selectOptionProvider);
                return menu.build();
            }

            SelectOptionProviderDefinition provider = optionalProvider.get();

            log.info("Executing select option provider {}", provider.getMethod().getName());
            SelectOptionEvent event = new SelectOptionEvent(provider);
            try {
                provider.getMethod().invoke(runtime.getInstance(), event);
            } catch (Exception exception) {
                throw new IllegalStateException("Auto complete execution failed!", exception);
            }

            menu.addOptions(event.getSelectOptions());
        }

        return menu.build();
    }

    /**
     * Gets a set of all {@link SelectOptionDefinition SelectOptionDefinitions}.
     *
     * @return a set of all {@link SelectOptionDefinition SelectOptionDefinitions}
     */
    public Set<SelectOptionDefinition> getSelectOptions() {
        return selectOptions;
    }

    public String getSelectOptionProvider() {
        return selectOptionProvider;
    }

    @Override
    public String toString() {
        return "StringSelectMenuDefinition{" +
               "selectOptions=" + selectOptions +
               ", selectOptionProvider='" + selectOptionProvider + '\'' +
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
