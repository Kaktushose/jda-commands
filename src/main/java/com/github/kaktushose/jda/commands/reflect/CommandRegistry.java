package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandRegistry {

    private final static Logger log = LoggerFactory.getLogger(CommandRegistry.class);
    private final ParameterAdapterRegistry parameterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final Set<ControllerDefinition> controllers;
    private final Set<CommandDefinition> commands;

    public CommandRegistry(ParameterAdapterRegistry parameterRegistry, ValidatorRegistry validatorRegistry) {
        this.parameterRegistry = parameterRegistry;
        this.validatorRegistry = validatorRegistry;
        controllers = new HashSet<>();
        commands = new HashSet<>();
    }

    public void index(String... packages) {
        log.debug("Indexing controllers...");

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forClass(getClass()))
                .filterInputsBy(new FilterBuilder().includePackage(packages));
        Reflections reflections = new Reflections(config);

        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(CommandController.class);

        for (Class<?> clazz : controllerSet) {
            log.debug("Found controller {}", clazz.getName());

            Optional<ControllerDefinition> optional = ControllerDefinition.build(clazz, parameterRegistry, validatorRegistry);

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

        log.info("Successfully registered {} controller(s) with a total of {} command(s)!", controllers.size(), commands.size());
    }

    public Set<ControllerDefinition> getControllers() {
        return controllers;
    }

    public Set<CommandDefinition> getCommands() {
        return commands;
    }
}
