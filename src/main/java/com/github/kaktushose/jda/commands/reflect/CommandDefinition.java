package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Cooldown;
import com.github.kaktushose.jda.commands.annotations.Permission;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Representation of a single command.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Command
 * @since 2.0.0
 */
public class CommandDefinition implements Comparable<CommandDefinition> {

    private static final Logger log = LoggerFactory.getLogger(CommandDefinition.class);
    private final List<String> labels;
    private final CommandMetadata metadata;
    private final List<ParameterDefinition> parameters;
    private final Set<String> permissions;
    private final CooldownDefinition cooldown;
    private final boolean isDM;
    private final Method method;
    private final Object instance;
    private boolean isSuper;
    private ControllerDefinition controller;

    private CommandDefinition(List<String> labels,
                              CommandMetadata metadata,
                              List<ParameterDefinition> parameters,
                              Set<String> permissions,
                              CooldownDefinition cooldown,
                              ControllerDefinition controller,
                              boolean isSuper,
                              boolean isDM,
                              Method method,
                              Object instance) {
        this.labels = labels;
        this.metadata = metadata;
        this.parameters = parameters;
        this.permissions = permissions;
        this.cooldown = cooldown;
        this.controller = controller;
        this.isSuper = isSuper;
        this.isDM = isDM;
        this.method = method;
        this.instance = instance;
    }

    /**
     * Builds a new CommandDefinition.
     *
     * @param method            the {@link Method} of the command
     * @param instance          an instance of the method defining class
     * @param adapterRegistry   the corresponding {@link TypeAdapterRegistry}
     * @param validatorRegistry the corresponding {@link ValidatorRegistry}
     * @return an {@link Optional} holding the CommandDefinition
     */
    public static Optional<CommandDefinition> build(@NotNull Method method,
                                                    @NotNull Object instance,
                                                    @NotNull TypeAdapterRegistry adapterRegistry,
                                                    @NotNull ValidatorRegistry validatorRegistry) {

        if (!method.isAnnotationPresent(Command.class) || !method.getDeclaringClass().isAnnotationPresent(CommandController.class)) {
            return Optional.empty();
        }

        Command command = method.getAnnotation(Command.class);
        CommandController commandController = method.getDeclaringClass().getAnnotation(CommandController.class);

        if (!command.isActive()) {
            log.debug("Command {} is set inactive. Skipping this command!", method.getName());
            return Optional.empty();
        }

        Set<String> permissions = new HashSet<>();
        if (method.isAnnotationPresent(Permission.class)) {
            Permission permission = method.getAnnotation(Permission.class);
            permissions = new HashSet<>(Arrays.asList(permission.value()));
        }

        // generate possible labels
        List<String> labels = new ArrayList<>();
        for (String controllerLabel : commandController.value()) {
            for (String commandLabel : command.value()) {
                String label = (controllerLabel + " " + commandLabel).trim();
                labels.add(label);
            }
        }

        // build parameter definitions
        List<ParameterDefinition> parameters = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            parameters.add(ParameterDefinition.build(parameter, validatorRegistry));
        }

        if (parameters.size() < 1) {
            logError(String.format("First parameter must be of type %s!", CommandEvent.class.getSimpleName()), method);
            return Optional.empty();
        }

        // validate parameter definitions
        boolean hasOptional = false;
        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition parameter = parameters.get(i);
            Class<?> type = parameter.getType();

            // first argument must be a CommandEvent
            if (i == 0) {
                if (!CommandEvent.class.isAssignableFrom(type)) {
                    logError(String.format("First parameter must be of type %s!", CommandEvent.class.getSimpleName()), method);
                    return Optional.empty();
                }
                continue;
            }

            // check if parameter adapter exists
            if (!adapterRegistry.exists(type)) {
                log.warn("No type adapter for type {} found! Command {}.{} cannot be executed in this state!",
                        type.getName(),
                        method.getDeclaringClass().getSimpleName(),
                        method.getName());
            }

            // argument parsing can be skipped by using just a String array (the traditional way of command frameworks)
            // this means that no other parameters are allowed in this case
            if (type.isAssignableFrom(String[].class) && parameters.size() > 2) {
                logError("Additional parameters aren't allowed when using arrays!", method);
                return Optional.empty();
            }

            // String concatenation is enabled => must be last parameter
            if (parameter.isConcat() && i != parameters.size() - 1) {
                logError("Concatenation may be only enabled for the last parameter", method);
                return Optional.empty();
            }

            // if method already had an optional parameter (hasOptional == true) this one has to be optional as well
            if (hasOptional && !parameter.isOptional()) {
                logError("An optional parameter must not be followed by a non-optional parameter!", method);
                return Optional.empty();
            }
            if (parameter.isOptional()) {
                // using primitives with default values results in NPEs. Warn the user about it
                if (parameter.getDefaultValue() == null && parameter.isPrimitive()) {
                    log.warn("Command {} has an optional primitive datatype parameter, but no default value is present! " +
                            "This will result in a NullPointerException if the command is executed without the optional parameter!", method.getName());
                }
                hasOptional = true;
            }
        }

        CommandMetadata metadata = CommandMetadata.build(command, commandController);

        if (metadata.getUsage().equals("N/A") || metadata.getUsage().isEmpty()) {
            StringBuilder usage = new StringBuilder("{prefix}");
            usage.append(labels.get(0));
            parameters.forEach(parameter -> {
                if (CommandEvent.class.isAssignableFrom(parameter.getType())) {
                    return;
                }
                if (parameter.isOptional()) {
                    usage.append(" ").append(String.format("(%s)", parameter.getName()));
                } else {
                    usage.append(" ").append(String.format("<%s>", parameter.getName()));
                }
            });
            metadata.setUsage(usage.toString());
        }

        return Optional.of(new CommandDefinition(
                labels,
                metadata,
                parameters,
                permissions,
                CooldownDefinition.build(method.getAnnotation(Cooldown.class)),
                null,
                command.isSuper(),
                command.isDM(),
                method,
                instance
        ));
    }

    private static void logError(String message, Method commandMethod) {
        log.error("An error has occurred! Skipping Command \"{}.{}\"\nCommand method has an invalid method signature! {}",
                commandMethod.getDeclaringClass().getSimpleName(),
                commandMethod.getName(),
                message);
    }

    /**
     * Transforms this command definition to a {@link CommandData}.
     *
     * @return the transformed {@link CommandData}
     */
    public CommandData toCommandData() {
        SlashCommandData command = Commands.slash(
                labels.get(0),
                metadata.getDescription().replaceAll("N/A", "no description")
        );
        parameters.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.getType())) {
                return;
            }
            command.addOptions(parameter.toOptionData());
        });
        return command;
    }

    /**
     * Gets a list of all command labels.
     *
     * @return a list of all command labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Gets the {@link CommandMetadata}.
     *
     * @return the {@link CommandMetadata}
     */
    public CommandMetadata getMetadata() {
        return metadata;
    }

    /**
     * Gets a possibly-empty list of all {@link ParameterDefinition ParameterDefinitions}.
     *
     * @return a possibly-empty list of all {@link ParameterDefinition ParameterDefinitions}
     */
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    /**
     * Gets a possibly-empty list of all {@link ParameterDefinition ParameterDefinitions}
     * excluding the {@link CommandEvent} at index 0.
     *
     * @return a possibly-empty list of all {@link ParameterDefinition ParameterDefinitions}  excluding the
     * {@link CommandEvent} at index 0
     */
    public List<ParameterDefinition> getActualParameters() {
        return parameters.subList(parameters.isEmpty() ? 0 : 1, parameters.size());
    }

    /**
     * Gets a set of permission Strings.
     *
     * @return set of permission Strings
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Gets the {@link CooldownDefinition}. This is never null, even if the command has no cooldown.
     *
     * @return the {@link CooldownDefinition}
     */
    public CooldownDefinition getCooldown() {
        return cooldown;
    }

    /**
     * Gets the {@link ControllerDefinition} this command is defined inside.
     *
     * @return the {@link ControllerDefinition}
     */
    @Nullable
    public ControllerDefinition getController() {
        return controller;
    }

    /**
     * Sets the {@link ControllerDefinition}.
     *
     * @param controller the {@link ControllerDefinition} to use
     */
    public void setController(ControllerDefinition controller) {
        this.controller = controller;
    }

    /**
     * Whether this command has a cooldown. More formally, checks if {@link CooldownDefinition#getDelay()} > 0.
     *
     * @return {@code true} if this command has a cooldown.
     */
    public boolean hasCooldown() {
        return getCooldown().getDelay() > 0;
    }

    /**
     * Whether this command is a super command.
     *
     * @return {@code true} if this command is a super command
     */
    public boolean isSuper() {
        return isSuper;
    }

    /**
     * Set whether this command is a super command.
     *
     * @param isSuper {@code true} if this command is a super command
     */
    public void setSuper(boolean isSuper) {
        this.isSuper = isSuper;
    }

    /**
     * Whether this command can be executed inside direct messages.
     *
     * @return {@code true} if this command can be executed inside direct messages
     */
    public boolean isDM() {
        return isDM;
    }

    /**
     * Gets the {@link Method} of the command.
     *
     * @return the {@link Method} of the command
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Gets an instance of the method defining class
     *
     * @return an instance of the method defining class
     */
    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "{" +
                "labels=" + labels +
                ", metadata=" + metadata +
                ", parameters=" + parameters +
                ", permissions=" + permissions +
                ", cooldown=" + cooldown +
                ", isSuper=" + isSuper +
                ", isDM=" + isDM +
                '}';
    }

    @Override
    public int compareTo(@NotNull CommandDefinition command) {
        return labels.get(0).compareTo(command.getLabels().get(0));
    }
}
