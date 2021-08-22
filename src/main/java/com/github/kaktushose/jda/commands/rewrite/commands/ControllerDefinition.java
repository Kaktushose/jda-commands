package com.github.kaktushose.jda.commands.rewrite.commands;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Cooldown;
import com.github.kaktushose.jda.commands.annotations.Permission;
import com.github.kaktushose.jda.commands.exceptions.CommandException;
import com.github.kaktushose.jda.commands.rewrite.parameter.adapter.ParameterAdapterRegistry;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class ControllerDefinition {

    private static final Logger log = LoggerFactory.getLogger(ControllerDefinition.class);
    private final CommandDefinition superCommand;
    private final List<CommandDefinition> commands;

    private ControllerDefinition(CommandDefinition superCommand,
                                 List<CommandDefinition> commands) {
        this.superCommand = superCommand;
        this.commands = commands;
    }

    public static Optional<ControllerDefinition> build(Class<?> controllerClass, ParameterAdapterRegistry registry) {
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
        if (controllerClass.isAnnotationPresent(Cooldown.class)) {
            cooldown = CooldownDefinition.build(controllerClass.getAnnotation(Cooldown.class));
        }

        // index commands
        CommandDefinition superCommand = null;
        List<CommandDefinition> commands = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            Optional<CommandDefinition> optional = CommandDefinition.build(method, instance, registry);

            if (!optional.isPresent()) {
                continue;
            }

            CommandDefinition commandDefinition = optional.get();

            // add controller level permissions
            commandDefinition.getPermissions().addAll(permissions);

            if (commandDefinition.isSuper()) {
                superCommand = commandDefinition;
                continue;
            }

            // TODO remove once command overloading is working
            if (commands.stream().flatMap(command -> command.getLabels().stream()).anyMatch(commandDefinition.getLabels()::contains)) {
                log.error("An error has occurred! Skipping Command {}!",
                        commandDefinition.getMethod().getName(),
                        new CommandException("The labels for the command are already registered!")
                );
                return Optional.empty();
            }

            if (commandDefinition.getCooldown().getDelay() == 0) {
                commandDefinition.getCooldown().set(cooldown);
            }

            commands.add(commandDefinition);

        }

        return Optional.of(new ControllerDefinition(superCommand, commands));
    }

    public boolean hasSuperCommand() {
        return superCommand != null;
    }

    public CommandDefinition getSuperCommand() {
        return superCommand;
    }

    public List<CommandDefinition> getCommands() {
        return commands;
    }

}
