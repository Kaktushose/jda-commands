package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central registry for all {@link SlashCommandDefinition CommandDefinitions}.
 *
 * @since 2.0.0
 */
public final class InteractionRegistry {

    private final static Logger log = LoggerFactory.getLogger(InteractionRegistry.class);
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final LocalizationFunction localizationFunction;
    private final Set<GenericInteractionDefinition> definitions = new HashSet<>();

    /**
     * Constructs a new CommandRegistry.
     *
     * @param validatorRegistry    the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector   the corresponding {@link DependencyInjector}
     * @param localizationFunction the {@link LocalizationFunction} to use
     */
    public InteractionRegistry(@NotNull ValidatorRegistry validatorRegistry,
                               @NotNull DependencyInjector dependencyInjector,
                               @NotNull LocalizationFunction localizationFunction) {
        this.validatorRegistry = validatorRegistry;
        this.dependencyInjector = dependencyInjector;
        this.localizationFunction = localizationFunction;
    }

    /**
     * Scans the whole classpath for commands.
     *
     * @param packages package(s) to exclusively scan
     * @param clazz    a class of the classpath to scan
     */
    public void index(@NotNull Class<?> clazz, @NotNull String... packages) {
        log.debug("Indexing controllers...");

        FilterBuilder filter = new FilterBuilder();
        for (String pkg : packages) {
            filter.includePackage(pkg);
        }

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated)
                .setUrls(ClasspathHelper.forClass(clazz))
                .filterInputsBy(filter);
        Reflections reflections = new Reflections(config);

        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(Interaction.class);

        for (Class<?> aClass : controllerSet) {
            log.debug("Found interaction controller {}", aClass.getName());

            Optional<InteractionControllerDefinition> optional = InteractionControllerDefinition.build(
                    aClass,
                    validatorRegistry,
                    dependencyInjector,
                    localizationFunction
            );

            if (optional.isEmpty()) {
                log.warn("Unable to index the interaction controller!");
                continue;
            }

            InteractionControllerDefinition controller = optional.get();
            definitions.addAll(controller.definitions());
            log.debug("Registered interaction controller {}", controller);
        }

        log.debug("Successfully registered {} interaction controller(s) with a total of {} interaction(s)!",
                controllerSet.size(),
                definitions.size());
    }

    /**
     * Gets a possibly-empty list of all {@link GenericCommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link GenericCommandDefinition CommandDefinitions}
     */
    public Collection<GenericCommandDefinition> getCommands() {
        return definitions.stream()
                .filter(GenericCommandDefinition.class::isInstance)
                .map(GenericCommandDefinition.class::cast)
                .toList();
    }

    /**
     * Gets a possibly-empty list of all buttons.
     *
     * @return a possibly-empty list of all buttons
     */
    public Collection<ButtonDefinition> getButtons() {
        return definitions.stream()
                .filter(ButtonDefinition.class::isInstance)
                .map(ButtonDefinition.class::cast)
                .toList();
    }

    /**
     * Gets a possibly-empty list of all select menus.
     *
     * @return a possibly-empty list of all select menus
     */
    public Collection<GenericSelectMenuDefinition<? extends SelectMenu>> getSelectMenus() {
        return definitions.stream()
                .filter(GenericSelectMenuDefinition.class::isInstance)
                .<GenericSelectMenuDefinition<? extends SelectMenu>>map(GenericSelectMenuDefinition.class::cast)
                .toList();
    }

    /**
     * Gets a possibly-empty list of all {@link AutoCompleteDefinition AutoCompleteDefinitions}.
     *
     * @return a possibly-empty list of all {@link AutoCompleteDefinition AutoCompleteDefinitions}
     */
    public Collection<AutoCompleteDefinition> getAutoCompletes() {
        return definitions.stream()
                .filter(AutoCompleteDefinition.class::isInstance)
                .map(AutoCompleteDefinition.class::cast)
                .toList();
    }

    /**
     * Gets a possibly-empty list of all {@link ModalDefinition ModalDefinitions}.
     *
     * @return a possibly-empty list of all {@link ModalDefinition ModalDefinitions}
     */
    public Collection<ModalDefinition> getModals() {
        return definitions.stream()
                .filter(ModalDefinition.class::isInstance)
                .map(ModalDefinition.class::cast)
                .toList();
    }
}
