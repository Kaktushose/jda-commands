package com.github.kaktushose.jda.commands.rewrite.reflect;

import com.github.kaktushose.jda.commands.rewrite.annotations.CommandController;
import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.ValidatorRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandRegistry {

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
        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forClass(getClass()))
                .filterInputsBy(new FilterBuilder().includePackage(packages));
        Reflections reflections = new Reflections(config);

        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(CommandController.class);

        for (Class<?> clazz : controllerSet) {
            Optional<ControllerDefinition> optional = ControllerDefinition.build(clazz, parameterRegistry, validatorRegistry);

            if (!optional.isPresent()) {
                continue;
            }

            ControllerDefinition controller = optional.get();
            controllers.add(controller);
        }
    }

    public Set<ControllerDefinition> getControllers() {
        return controllers;
    }

}
