package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private final Set<MenuOptionDefinition> selectOptions;
    private final String dynamicOptionResolver;

    protected StringSelectMenuDefinition(Method method,
                                         Set<String> permissions,
                                         boolean ephemeral,
                                         Set<MenuOptionDefinition> selectOptions,
                                         String dynamicOptionResolver,
                                         String placeholder,
                                         int minValue,
                                         int maxValue) {
        super(method, permissions, ephemeral, placeholder, minValue, maxValue);
        this.selectOptions = selectOptions;
        this.dynamicOptionResolver = dynamicOptionResolver;
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

        Set<MenuOptionDefinition> selectOptions = new HashSet<>();
        for (MenuOption option : method.getDeclaredAnnotationsByType(MenuOption.class)) {
            selectOptions.add(MenuOptionDefinition.build(option));
        }

        String optionResolver = null;
        if (method.isAnnotationPresent(DynamicOptions.class)) {
            DynamicOptions dynamicOptions = method.getAnnotation(DynamicOptions.class);
            if (!dynamicOptions.value().isBlank()) {
                optionResolver = dynamicOptions.value();
            }
        }

        if (selectOptions.isEmpty() && optionResolver == null) {
            log.error("An error has occurred! Skipping Select Menu {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("Cannot build select menu without options! Either use %s or %s",
                            MenuOption.class.getSimpleName(),
                            DynamicOptions.class.getSimpleName())
                    ));
            return Optional.empty();
        }

        return Optional.of(new StringSelectMenuDefinition(
                method,
                permissions,
                selectMenu.ephemeral(),
                selectOptions,
                optionResolver,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    @SuppressWarnings("unchecked")
    public net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu toSelectMenu(RuntimeSupervisor.InteractionRuntime runtime, boolean enabled, InteractionRegistry interactionRegistry) {
        var menu = net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.create(createCustomId(runtime.getRuntimeId()))
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue)
                .setDefaultOptions(selectOptions.stream()
                        .filter(MenuOptionDefinition::isDefault)
                        .map(MenuOptionDefinition::toSelectOption)
                        .collect(Collectors.toSet())
                )
                .setDisabled(!enabled);

        menu.addOptions(selectOptions.stream().map(MenuOptionDefinition::toSelectOption).collect(Collectors.toSet()));

        if (dynamicOptionResolver != null) {
            Optional<DynamicOptionResolverDefinition> optionalResolver = interactionRegistry
                    .getDynamicOptionResolvers()
                    .stream()
                    .filter(it -> String.format("%s%s", it.getMethod().getDeclaringClass().getSimpleName(), dynamicOptionResolver).equals(it.getDefinitionId()))
                    .findFirst();

            if (optionalResolver.isEmpty()) {
                log.warn("DynamicOptionResolver {} not found!", dynamicOptionResolver);
                return menu.build();
            }

            DynamicOptionResolverDefinition resolver = optionalResolver.get();

            log.info("Executing DynamicOptionResolver {}", resolver.getMethod().getName());
            try {
                Object selectOptions = resolver.getMethod().invoke(runtime.getInstance());
                System.out.println(selectOptions);
                if (Set.class.isAssignableFrom(selectOptions.getClass())) {
                    menu.addOptions((Set<SelectOption>) selectOptions);
                } else {
                    log.error("Method '{}' has wrong return type. Return type must be Set<SelectOption>!", resolver.getMethod().getName());
                }
            } catch (Exception exception) {
                throw new IllegalStateException("Resolving menu options failed", exception);
            }
        }

        return menu.build();
    }

    /**
     * Gets a set of all {@link MenuOptionDefinition SelectOptionDefinitions}.
     *
     * @return a set of all {@link MenuOptionDefinition SelectOptionDefinitions}
     */
    public Set<MenuOptionDefinition> getSelectOptions() {
        return selectOptions;
    }

    /**
     * Gets the name of the {@link DynamicOptionResolver} bound to this definition. Returns {@code null} if no
     * {@link DynamicOptionResolver} was defined.
     *
     * @return the name of the {@link DynamicOptionResolver}
     */
    public @Nullable String getDynamicOptionResolverName() {
        return dynamicOptionResolver;
    }

    @Override
    public String toString() {
        return "StringSelectMenuDefinition{" +
               "selectOptions=" + selectOptions +
               ", dynamicOptionResolver='" + dynamicOptionResolver + '\'' +
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
