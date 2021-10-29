package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Permission;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class ControllerDefinition {

    private static final Logger log = LoggerFactory.getLogger(ControllerDefinition.class);
    private final List<CommandDefinition> superCommands;
    private final List<CommandDefinition> subCommands;

    private ControllerDefinition(List<CommandDefinition> superCommands,
                                 List<CommandDefinition> subCommands) {
        this.superCommands = superCommands;
        this.subCommands = subCommands;
    }

    public static Optional<ControllerDefinition> build(Class<?> controllerClass,
                                                       TypeAdapterRegistry adapterRegistry,
                                                       ValidatorRegistry validatorRegistry) {
        CommandController commandController = controllerClass.getAnnotation(CommandController.class);

        if (!commandController.isActive()) {
            log.warn("CommandController {} is set inactive. Skipping the controller and its commands", controllerClass.getName());
            return Optional.empty();
        }

        // create instance of class
        Object instance;
        try {
            instance = controllerClass.getConstructors()[0].newInstance();
        } catch (Exception e) {
            log.error("Unable to create controller instance!", e);
            return Optional.empty();
        }

        // TODO Dependency Injection

        // index controller level permissions
        Set<String> permissions = new HashSet<>();
        if (controllerClass.isAnnotationPresent(Permission.class)) {
            Permission permission = controllerClass.getAnnotation(Permission.class);
            permissions = Sets.newHashSet(permission.value());
        }

        // get controller level cooldown and use it if no command level cooldown is present
        CooldownDefinition cooldown = null;
        if (controllerClass.isAnnotationPresent(com.github.kaktushose.jda.commands.annotations.Cooldown.class)) {
            cooldown = CooldownDefinition.build(controllerClass.getAnnotation(com.github.kaktushose.jda.commands.annotations.Cooldown.class));
        }

        // index commands
        List<CommandDefinition> superCommands = new ArrayList<>();
        List<CommandDefinition> commands = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            Optional<CommandDefinition> optional = CommandDefinition.build(method, instance, adapterRegistry, validatorRegistry);

            if (!optional.isPresent()) {
                continue;
            }
            CommandDefinition commandDefinition = optional.get();

            // add controller level permissions
            commandDefinition.getPermissions().addAll(permissions);

            // TODO remove once command overloading is working
            if (commands.stream().flatMap(command -> command.getLabels().stream()).anyMatch(commandDefinition.getLabels()::contains)) {
                log.error("An error has occurred! Skipping Command {}.{}!",
                        commandController.getClass().getName(),
                        commandDefinition.getMethod().getName(),
                        new IllegalArgumentException("The labels for the command are already registered!")
                );
                continue;
            }

            if (commandDefinition.getCooldown().getDelay() == 0) {
                commandDefinition.getCooldown().set(cooldown);
            }

            if (commandDefinition.isSuper()) {
                superCommands.add(commandDefinition);
                continue;
            }
            commands.add(commandDefinition);
        }
        ControllerDefinition controller = new ControllerDefinition(superCommands, commands);
        controller.getSuperCommands().forEach(definition -> definition.setController(controller));
        controller.getSubCommands().forEach(definition -> definition.setController(controller));
        return Optional.of(controller);
    }

    public boolean hasSuperCommand() {
        return superCommands != null;
    }

    public List<CommandDefinition> getSuperCommands() {
        return superCommands;
    }

    public List<CommandDefinition> getSubCommands() {
        return subCommands;
    }

    @Override
    public String toString() {
        return "ControllerDefinition{" +
                "superCommands=" + superCommands +
                ", subCommands=" + subCommands +
                '}';
    }
}
