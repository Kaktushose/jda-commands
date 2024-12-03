package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Inject;
import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.StringSelectMenuDefinition;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.*;

/**
 * Representation of an interaction controller.
 *
 * @since 2.0.0
 */
public record InteractionControllerDefinition(
        Set<GenericInteractionDefinition> definitions
) {

    private static final Logger log = LoggerFactory.getLogger(InteractionControllerDefinition.class);

    /**
     * Builds a new ControllerDefinition.
     *
     * @param interactionClass     the {@link Class} of the controller
     * @param validatorRegistry    the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector   the corresponding {@link DependencyInjector}
     * @param localizationFunction the {@link LocalizationFunction} to use
     * @return an {@link Optional} holding the ControllerDefinition
     */
    public static InteractionControllerDefinition build(@NotNull Class<?> interactionClass,
                                                        @NotNull ValidatorRegistry validatorRegistry,
                                                        @NotNull DependencyInjector dependencyInjector,
                                                        @NotNull LocalizationFunction localizationFunction) {
        Interaction interaction = interactionClass.getAnnotation(Interaction.class);

        List<Field> fields = Arrays.stream(interactionClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .toList();
        dependencyInjector.registerDependencies(interactionClass, fields);

        Permissions permission = interactionClass.getAnnotation(Permissions.class);
        final Set<String> permissions = permission != null
                ? Set.of(permission.value())
                : Set.of();

        // get controller level cooldown and use it if no command level cooldown is present
        Cooldown cooldownAnn = interactionClass.getAnnotation(Cooldown.class);
        CooldownDefinition cooldown = cooldownAnn != null
                ? CooldownDefinition.build(cooldownAnn)
                : null;


        Collection<AutoCompleteDefinition> autoCompleteDefinitions = autoCompleteDefinitions(interactionClass);

        // index interactions
        Set<GenericInteractionDefinition> interactionDefinitions = interactionDefinitions(
                interactionClass,
                validatorRegistry,
                localizationFunction,
                interaction,
                permissions,
                cooldown,
                autoCompleteDefinitions
        );

        // validate auto completes
        List<SlashCommandDefinition> commandDefinitions = interactionDefinitions.stream()
                .filter(SlashCommandDefinition.class::isInstance)
                .map(SlashCommandDefinition.class::cast)
                .toList();

        autoCompleteDefinitions.stream()
                .map(AutoCompleteDefinition::getCommandNames)
                .flatMap(Collection::stream)
                .filter(name -> commandDefinitions.stream().noneMatch(command -> command.getName().startsWith(name)))
                .forEach(s -> log.warn("No Command found for auto complete {}", s));

        return new InteractionControllerDefinition(interactionDefinitions);
    }

    private static Collection<AutoCompleteDefinition> autoCompleteDefinitions(Class<?> interactionClass) {
        return Arrays.stream(interactionClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AutoComplete.class))
                .map(AutoCompleteDefinition::build)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Set<GenericInteractionDefinition> interactionDefinitions(Class<?> clazz,
                                                                            ValidatorRegistry validatorRegistry,
                                                                            LocalizationFunction localizationFunction,
                                                                            Interaction interaction,
                                                                            Set<String> permissions,
                                                                            CooldownDefinition cooldown,
                                                                            Collection<AutoCompleteDefinition> autocompletes) {
        Set<GenericInteractionDefinition> definitions = new HashSet<>(autocompletes);
        for (Method method : clazz.getDeclaredMethods()) {
            final MethodBuildContext context = new MethodBuildContext(
                    validatorRegistry,
                    localizationFunction,
                    interaction,
                    permissions,
                    cooldown,
                    method,
                    autocompletes
            );

            Optional<? extends GenericInteractionDefinition> definition = Optional.empty();
            // index commands
            if (method.isAnnotationPresent(SlashCommand.class)) {
                definition = SlashCommandDefinition.build(context);
            }
            if (method.isAnnotationPresent(ContextCommand.class)) {
                definition = ContextCommandDefinition.build(context);
            }

            // index components
            if (method.isAnnotationPresent(Button.class)) {
                definition = ButtonDefinition.build(context);
            }
            if (method.isAnnotationPresent(EntitySelectMenu.class)) {
                definition = EntitySelectMenuDefinition.build(context);
            }
            if (method.isAnnotationPresent(StringSelectMenu.class)) {
                definition = StringSelectMenuDefinition.build(context);
            }

            //index modals
            if (method.isAnnotationPresent(Modal.class)) {
                definition = ModalDefinition.build(method);
            }

            definition.ifPresent(definitions::add);
        }
        return definitions;
    }
}
