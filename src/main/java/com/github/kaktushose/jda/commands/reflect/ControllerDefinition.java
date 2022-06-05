package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Inject;
import com.github.kaktushose.jda.commands.annotations.Permission;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Representation of a command controller.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class ControllerDefinition {

    private static final Logger log = LoggerFactory.getLogger(ControllerDefinition.class);
    private final List<CommandDefinition> superCommands;
    private final List<CommandDefinition> subCommands;
    private final List<ButtonDefinition> buttons;

    private ControllerDefinition(List<CommandDefinition> superCommands,
                                 List<CommandDefinition> subCommands,
                                 List<ButtonDefinition> buttons) {
        this.superCommands = superCommands;
        this.subCommands = subCommands;
        this.buttons = buttons;
    }

    /**
     * Builds a new ControllerDefinition.
     *
     * @param controllerClass    the {@link Class} of the controller
     * @param adapterRegistry    the corresponding {@link TypeAdapterRegistry}
     * @param validatorRegistry  the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector the corresponding {@link DependencyInjector}
     * @return an {@link Optional} holding the ControllerDefinition
     */
    public static Optional<ControllerDefinition> build(@NotNull Class<?> controllerClass,
                                                       @NotNull TypeAdapterRegistry adapterRegistry,
                                                       @NotNull ValidatorRegistry validatorRegistry,
                                                       @NotNull DependencyInjector dependencyInjector) {
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

        List<Field> fields = new ArrayList<>();
        for (Field field : controllerClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            fields.add(field);
        }
        dependencyInjector.registerDependencies(instance, fields);

        // index controller level permissions
        Set<String> permissions = new HashSet<>();
        if (controllerClass.isAnnotationPresent(Permission.class)) {
            Permission permission = controllerClass.getAnnotation(Permission.class);
            permissions = new HashSet<>(Arrays.asList(permission.value()));
        }

        // get controller level cooldown and use it if no command level cooldown is present
        CooldownDefinition cooldown = null;
        if (controllerClass.isAnnotationPresent(com.github.kaktushose.jda.commands.annotations.Cooldown.class)) {
            cooldown = CooldownDefinition.build(controllerClass.getAnnotation(com.github.kaktushose.jda.commands.annotations.Cooldown.class));
        }

        // index commands
        List<CommandDefinition> superCommands = new ArrayList<>();
        List<CommandDefinition> subCommands = new ArrayList<>();
        List<ButtonDefinition> buttons = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            ButtonDefinition.build(method, instance).ifPresent(buttons::add);

            Optional<CommandDefinition> optional = CommandDefinition.build(method, instance, adapterRegistry, validatorRegistry);

            if (!optional.isPresent()) {
                continue;
            }
            CommandDefinition commandDefinition = optional.get();

            // add controller level permissions
            commandDefinition.getPermissions().addAll(permissions);
            commandDefinition.setDefaultEnabled(commandDefinition.isDefaultEnabled() && commandController.defaultEnable());

            commandDefinition.setEphemeral(commandDefinition.isEphemeral() || commandController.ephemeral());

            if (subCommands.stream().flatMap(command -> command.getLabels().stream()).anyMatch(commandDefinition.getLabels()::contains)) {
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
            subCommands.add(commandDefinition);
        }

        // if we only have one sub command and no super commands make it implicit a super command
        if (subCommands.size() == 1 && superCommands.size() == 0) {
            CommandDefinition command = subCommands.get(0);
            command.setSuper(true);
            superCommands.add(command);
            subCommands.clear();
        }

        ControllerDefinition controller = new ControllerDefinition(superCommands, subCommands, buttons);
        controller.getSuperCommands().forEach(definition -> definition.setController(controller));
        controller.getSubCommands().forEach(definition -> definition.setController(controller));
        return Optional.of(controller);
    }

    /**
     * Whether this controller has super commands.
     *
     * @return {@code true} if this controller has super commands
     */
    public boolean hasSuperCommands() {
        return superCommands != null;
    }

    /**
     * Gets a possibly-empty list of all super commands.
     *
     * @return a possibly-empty list of all super commands
     */
    public List<CommandDefinition> getSuperCommands() {
        return superCommands;
    }

    /**
     * Gets a possibly-empty list of all sub commands.
     *
     * @return a possibly-empty list of all sub commands
     */
    public List<CommandDefinition> getSubCommands() {
        return subCommands;
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
                "superCommands=" + superCommands +
                ", subCommands=" + subCommands +
                ", buttons=" + buttons +
                '}';
    }
}
