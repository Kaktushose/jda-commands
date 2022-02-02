package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
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
public class CommandRegistry {

    private final static Logger log = LoggerFactory.getLogger(CommandRegistry.class);
    private final TypeAdapterRegistry parameterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final Set<ControllerDefinition> controllers;
    private final Set<CommandDefinition> commands;

    /**
     * Constructs a new CommandRegistry.
     *
     * @param adapterRegistry    the corresponding {@link TypeAdapterRegistry}
     * @param validatorRegistry  the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector the corresponding {@link DependencyInjector}
     */
    public CommandRegistry(@NotNull TypeAdapterRegistry adapterRegistry,
                           @NotNull ValidatorRegistry validatorRegistry,
                           @NotNull DependencyInjector dependencyInjector) {
        this.parameterRegistry = adapterRegistry;
        this.validatorRegistry = validatorRegistry;
        this.dependencyInjector = dependencyInjector;
        controllers = new HashSet<>();
        commands = new HashSet<>();
    }

    /**
     * Scans the whole classpath for commands.
     *
     * @param packages package(s) to exclusively scan
     * @param clazz    a class of the classpath to scan
     */
    public void index(@NotNull Class<?> clazz, @NotNull String... packages) {
        log.debug("Indexing controllers...");

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forClass(clazz))
                .filterInputsBy(new FilterBuilder().includePackage(packages));
        Reflections reflections = new Reflections(config);

        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(CommandController.class);

        for (Class<?> aClass : controllerSet) {
            log.debug("Found controller {}", aClass.getName());

            Optional<ControllerDefinition> optional = ControllerDefinition.build(aClass,
                    parameterRegistry,
                    validatorRegistry,
                    dependencyInjector
            );

            if (!optional.isPresent()) {
                log.warn("Unable to index the controller!");
                continue;
            }

            ControllerDefinition controller = optional.get();
            controllers.add(controller);
            commands.addAll(controller.getSuperCommands());
            commands.addAll(controller.getSubCommands());

            log.debug("Registered controller {}", controller);
        }

        log.debug("Successfully registered {} controller(s) with a total of {} command(s)!", controllers.size(), commands.size());
    }

    /**
     * Gets a list of all {@link ControllerDefinition ControllerDefinitions}.
     *
     * @return a list of all {@link ControllerDefinition ControllerDefinitions}
     */
    public Set<ControllerDefinition> getControllers() {
        return Collections.unmodifiableSet(controllers);
    }

    /**
     * Gets a list of all {@link CommandDefinition CommandDefinitions}.
     *
     * @return a list of all {@link CommandDefinition CommandDefinitions}
     */
    public Set<CommandDefinition> getCommands() {
        return Collections.unmodifiableSet(commands);
    }
}
