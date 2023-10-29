package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.*;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.GenericSelectMenuDefinition;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Central registry for all {@link SlashCommandDefinition CommandDefinitions}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class InteractionRegistry {

    private final static Logger log = LoggerFactory.getLogger(InteractionRegistry.class);
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final LocalizationFunction localizationFunction;
    private final Set<InteractionDefinition> controllers;
    private final Set<SlashCommandDefinition> commands;
    private final Set<ButtonDefinition> buttons;
    private final Set<GenericSelectMenuDefinition<? extends SelectMenu>> selectMenus;
    private final Set<AutoCompleteDefinition> autoCompletes;
    private final Set<ContextCommandDefinition> contextMenus;
    private final Set<ModalDefinition> modals;

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
        controllers = new HashSet<>();
        commands = new HashSet<>();
        buttons = new HashSet<>();
        selectMenus = new HashSet<>();
        autoCompletes = new HashSet<>();
        contextMenus = new HashSet<>();
        modals = new HashSet<>();
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
            log.debug("Found controller {}", aClass.getName());

            Optional<InteractionDefinition> optional = InteractionDefinition.build(
                    aClass,
                    validatorRegistry,
                    dependencyInjector,
                    localizationFunction
            );

            if (optional.isEmpty()) {
                log.warn("Unable to index the controller!");
                continue;
            }

            InteractionDefinition controller = optional.get();
            controllers.add(controller);
            commands.addAll(controller.getCommands());
            buttons.addAll(controller.getButtons());
            selectMenus.addAll(controller.getSelectMenus());
            autoCompletes.addAll(controller.getAutoCompletes());
            contextMenus.addAll(controller.getContextMenus());
            modals.addAll(controller.getModals());
            log.debug("Registered controller {}", controller);
        }

        log.debug("Successfully registered {} controller(s) with a total of {} interaction(s)!",
                controllers.size(),
                commands.size() + buttons.size());
    }

    /**
     * Gets a possibly-empty list of all {@link InteractionDefinition ControllerDefinitions}.
     *
     * @return a possibly-empty list of all {@link InteractionDefinition ControllerDefinitions}
     */
    public Set<InteractionDefinition> getControllers() {
        return Collections.unmodifiableSet(controllers);
    }

    /**
     * Gets a possibly-empty list of all {@link SlashCommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link SlashCommandDefinition CommandDefinitions}
     */
    public Set<SlashCommandDefinition> getCommands() {
        return Collections.unmodifiableSet(commands);
    }

    /**
     * Gets a possibly-empty list of all {@link ButtonDefinition ButtonDefinitions}.
     *
     * @return a possibly-empty list of all {@link ButtonDefinition ButtonDefinitions}
     */
    public Set<ButtonDefinition> getButtons() {
        return Collections.unmodifiableSet(buttons);
    }

    /**
     * Gets a possibly-empty list of all {@link AutoCompleteDefinition AutoCompleteDefinitions}.
     *
     * @return a possibly-empty list of all {@link AutoCompleteDefinition AutoCompleteDefinitions}
     */
    public Set<AutoCompleteDefinition> getAutoCompletes() {
        return Collections.unmodifiableSet(autoCompletes);
    }

    /**
     * Gets a possibly-empty list of all {@link ButtonDefinition ButtonDefinitions}.
     *
     * @return a possibly-empty list of all {@link ButtonDefinition ButtonDefinitions}
     */
    public Set<GenericSelectMenuDefinition<? extends SelectMenu>> getSelectMenus() {
        return Collections.unmodifiableSet(selectMenus);
    }

    public Set<ContextCommandDefinition> getContextMenus() {
        return Collections.unmodifiableSet(contextMenus);
    }

    public Set<ModalDefinition> getModals() {
        return Collections.unmodifiableSet(modals);
    }
}
