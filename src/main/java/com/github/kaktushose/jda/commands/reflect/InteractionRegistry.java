package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
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
 * Central registry for all {@link CommandDefinition CommandDefinitions}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class InteractionRegistry {

    private final static Logger log = LoggerFactory.getLogger(InteractionRegistry.class);
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final Set<ControllerDefinition> controllers;
    private final Set<CommandDefinition> commands;
    private final Set<ButtonDefinition> buttons;

    /**
     * Constructs a new CommandRegistry.
     *
     * @param validatorRegistry  the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector the corresponding {@link DependencyInjector}
     */
    public InteractionRegistry(@NotNull ValidatorRegistry validatorRegistry, @NotNull DependencyInjector dependencyInjector) {
        this.validatorRegistry = validatorRegistry;
        this.dependencyInjector = dependencyInjector;
        controllers = new HashSet<>();
        commands = new HashSet<>();
        buttons = new HashSet<>();
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

            Optional<ControllerDefinition> optional = ControllerDefinition.build(aClass, validatorRegistry, dependencyInjector);

            if (!optional.isPresent()) {
                log.warn("Unable to index the controller!");
                continue;
            }

            ControllerDefinition controller = optional.get();
            controllers.add(controller);
            commands.addAll(controller.getCommands());
            buttons.addAll(controller.getButtons());

            log.debug("Registered controller {}", controller);
        }

        log.debug("Successfully registered {} controller(s) with a total of {} interaction(s)!",
                controllers.size(),
                commands.size() + buttons.size());
    }

    /**
     * Gets a possibly-empty list of all {@link ControllerDefinition ControllerDefinitions}.
     *
     * @return a possibly-empty list of all {@link ControllerDefinition ControllerDefinitions}
     */
    public Set<ControllerDefinition> getControllers() {
        return Collections.unmodifiableSet(controllers);
    }

    /**
     * Gets a possibly-empty list of all {@link CommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link CommandDefinition CommandDefinitions}
     */
    public Set<CommandDefinition> getCommands() {
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

}
