package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Inject;
import com.github.kaktushose.jda.commands.annotations.Permission;
import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of a interaction controller.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 2.0.0
 */
public class ControllerDefinition {

    private static final Logger log = LoggerFactory.getLogger(ControllerDefinition.class);
    private final List<CommandDefinition> commands;
    private final List<ButtonDefinition> buttons;

    private ControllerDefinition(List<CommandDefinition> commands,
                                 List<ButtonDefinition> buttons) {
        this.commands = commands;
        this.buttons = buttons;
    }

    /**
     * Builds a new ControllerDefinition.
     *
     * @param controllerClass    the {@link Class} of the controller
     * @param validatorRegistry  the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector the corresponding {@link DependencyInjector}
     * @return an {@link Optional} holding the ControllerDefinition
     */
    public static Optional<ControllerDefinition> build(@NotNull Class<?> controllerClass,
                                                       @NotNull ValidatorRegistry validatorRegistry,
                                                       @NotNull DependencyInjector dependencyInjector) {
        Interaction interaction = controllerClass.getAnnotation(Interaction.class);

        if (!interaction.isActive()) {
            log.warn("Interaction class {} is set inactive. Skipping the controller and its commands", controllerClass.getName());
            return Optional.empty();
        }

        List<Field> fields = new ArrayList<>();
        for (Field field : controllerClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            fields.add(field);
        }
        dependencyInjector.registerDependencies(controllerClass, fields);

        Set<String> permissions = new HashSet<>();
        // index controller level permissions
        if (controllerClass.isAnnotationPresent(Permission.class)) {
            Permission permission = controllerClass.getAnnotation(Permission.class);
            permissions = Arrays.stream(permission.value()).collect(Collectors.toSet());
        }

        // get controller level cooldown and use it if no command level cooldown is present
        CooldownDefinition cooldown = null;
        if (controllerClass.isAnnotationPresent(com.github.kaktushose.jda.commands.annotations.Cooldown.class)) {
            cooldown = CooldownDefinition.build(controllerClass.getAnnotation(com.github.kaktushose.jda.commands.annotations.Cooldown.class));
        }

        // index interactions
        List<CommandDefinition> commands = new ArrayList<>();
        List<ButtonDefinition> buttons = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {

            if (method.isAnnotationPresent(SlashCommand.class)) {
                Optional<CommandDefinition> optional = CommandDefinition.build(method, validatorRegistry);
                if (optional.isEmpty()) {
                    continue;
                }
                CommandDefinition commandDefinition = optional.get();

                // add controller level permissions
                commandDefinition.getPermissions().addAll(permissions);
                if (commandDefinition.getCooldown().getDelay() == 0) {
                    commandDefinition.getCooldown().set(cooldown);
                }

                if (interaction.ephemeral()) {
                    commandDefinition.setEphemeral(true);
                }

                commands.add(commandDefinition);
            }

            if (method.isAnnotationPresent(Button.class)) {
                ButtonDefinition.build(method).ifPresent(buttons::add);
            }
        }

        return Optional.of(new ControllerDefinition(commands, buttons));
    }

    /**
     * Gets a possibly-empty list of all commands.
     *
     * @return a possibly-empty list of all commands
     */
    public List<CommandDefinition> getCommands() {
        return commands;
    }

    /**
     * Gets a possibly-empty list of all buttons.
     *
     * @return a possibly-empty list of all buttons
     */
    public List<ButtonDefinition> getButtons() {
        return buttons;
    }

    @Override
    public String toString() {
        return "ControllerDefinition{" +
                "commands=" + commands +
                ", buttons=" + buttons +
                '}';
    }
}
