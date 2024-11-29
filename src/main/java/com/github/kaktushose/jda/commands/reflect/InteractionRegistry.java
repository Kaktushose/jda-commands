package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.MenuOptionProviderDefinition;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central registry for all {@link SlashCommandDefinition CommandDefinitions}.
 *
 * @since 2.0.0
 */
public class InteractionRegistry {

    private final static Logger log = LoggerFactory.getLogger(InteractionRegistry.class);
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final LocalizationFunction localizationFunction;
    private final Set<InteractionControllerDefinition> controllers;
    private final Set<GenericCommandDefinition> commands;
    private final Set<ButtonDefinition> buttons;
    private final Set<GenericSelectMenuDefinition<? extends SelectMenu>> selectMenus;
    private final Set<AutoCompleteDefinition> autoCompletes;
    private final Set<ModalDefinition> modals;
    private final Set<MenuOptionProviderDefinition> optionProviders;

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
        modals = new HashSet<>();
        optionProviders = new HashSet<>();
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
            controllers.add(controller);
            commands.addAll(controller.getCommands());
            buttons.addAll(controller.getButtons());
            selectMenus.addAll(controller.getSelectMenus());
            autoCompletes.addAll(controller.getAutoCompletes());
            modals.addAll(controller.getModals());
            optionProviders.addAll(controller.getMenuOptionProviders());
            log.debug("Registered interaction controller {}", controller);
        }

        log.debug("Successfully registered {} interaction controller(s) with a total of {} interaction(s)!",
                controllers.size(),
                commands.size() + buttons.size());
    }

    /**
     * Gets a possibly-empty list of all {@link InteractionControllerDefinition ControllerDefinitions}.
     *
     * @return a possibly-empty list of all {@link InteractionControllerDefinition ControllerDefinitions}
     */
    public Set<InteractionControllerDefinition> getInteractionControllers() {
        return Collections.unmodifiableSet(controllers);
    }

    /**
     * Gets a possibly-empty list of all {@link GenericCommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link GenericCommandDefinition CommandDefinitions}
     */
    public Set<GenericCommandDefinition> getCommands() {
        return Collections.unmodifiableSet(commands);
    }

    /**
     * Gets a possibly-empty list of all {@link SlashCommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link SlashCommandDefinition CommandDefinitions}
     */
    public Set<SlashCommandDefinition> getSlashCommands() {
        return commands.stream().filter(it -> (it.getCommandType() == Command.Type.SLASH))
                .map(it -> (SlashCommandDefinition) it)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets a possibly-empty list of all {@link ContextCommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link ContextCommandDefinition CommandDefinitions}
     */
    public Set<ContextCommandDefinition> getContextCommands() {
        return commands.stream().filter(it ->
                        (it.getCommandType() == Command.Type.USER) || it.getCommandType() == Command.Type.MESSAGE)
                .map(it -> (ContextCommandDefinition) it)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets a possibly-empty Set of all {@link ButtonDefinition ButtonDefinitions}.
     *
     * @return a possibly-empty Set of all {@link ButtonDefinition ButtonDefinitions}
     */
    public Set<ButtonDefinition> getButtons() {
        return Collections.unmodifiableSet(buttons);
    }

    /**
     * Gets a possibly-empty Set of all {@link AutoCompleteDefinition AutoCompleteDefinitions}.
     *
     * @return a possibly-empty Set of all {@link AutoCompleteDefinition AutoCompleteDefinitions}
     */
    public Set<AutoCompleteDefinition> getAutoCompletes() {
        return Collections.unmodifiableSet(autoCompletes);
    }

    /**
     * Gets a possibly-empty Set of all {@link GenericSelectMenuDefinition SelectMenuDefinitions}.
     *
     * @return a possibly-empty Set of all {@link GenericSelectMenuDefinition SelectMenuDefinitions}
     */
    public Set<GenericSelectMenuDefinition<? extends SelectMenu>> getSelectMenus() {
        return Collections.unmodifiableSet(selectMenus);
    }

    /**
     * Gets a possibly-empty Set of all {@link ModalDefinition ModalDefinitions}.
     *
     * @return a possibly-empty Set of all {@link ModalDefinition ModalDefinitions}
     */
    public Set<ModalDefinition> getModals() {
        return Collections.unmodifiableSet(modals);
    }

    /**
     * Gets a possibly-empty Set of all {@link MenuOptionProviderDefinition MenuOptionProviderDefinitions}.
     *
     * @return a possibly-empty Set of all {@link MenuOptionProviderDefinition MenuOptionProviderDefinitions}
     */
    public Set<MenuOptionProviderDefinition> getMenuOptionProviders() {
        return optionProviders;
    }
}
