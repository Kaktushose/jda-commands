package com.github.kaktushose.jda.commands.reflect.interactions.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.CooldownDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of a slash command.
 *
 * @see SlashCommand
 * @since 2.0.0
 */
public class SlashCommandDefinition extends GenericCommandDefinition {

    private final String description;
    private final List<ParameterDefinition> parameters;
    private final CooldownDefinition cooldown;
    private boolean isAutoComplete;

    public SlashCommandDefinition(Method method,
                                  boolean ephemeral,
                                  String name,
                                  Set<String> permissions,
                                  boolean isGuildOnly,
                                  boolean isNSFW,
                                  Command.Type commandType,
                                  Set<Permission> enabledPermissions,
                                  SlashCommand.CommandScope scope,
                                  LocalizationFunction localizationFunction,
                                  String description,
                                  List<ParameterDefinition> parameters,
                                  CooldownDefinition cooldown,
                                  boolean isAutoComplete) {
        super(method, ephemeral, name, permissions, isGuildOnly, isNSFW, commandType, enabledPermissions, scope, localizationFunction);
        this.description = description;
        this.parameters = parameters;
        this.cooldown = cooldown;
        this.isAutoComplete = isAutoComplete;
    }

    /**
     * Builds a new CommandDefinition.
     *
     * @param method               the {@link Method} of the command
     * @param validatorRegistry    the corresponding {@link ValidatorRegistry}
     * @param localizationFunction the {@link LocalizationFunction} to use
     * @return an {@link Optional} holding the CommandDefinition
     */
    public static Optional<SlashCommandDefinition> build(@NotNull Method method,
                                                         @NotNull ValidatorRegistry validatorRegistry,
                                                         @NotNull LocalizationFunction localizationFunction) {

        if (!method.isAnnotationPresent(SlashCommand.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        SlashCommand command = method.getAnnotation(SlashCommand.class);
        Interaction interaction = method.getDeclaringClass().getAnnotation(Interaction.class);

        if (!command.isActive()) {
            log.debug("Command {} is set inactive. Skipping this command!", method.getName());
            return Optional.empty();
        }

        Set<String> permissions = new HashSet<>();
        if (method.isAnnotationPresent(Permissions.class)) {
            Permissions permission = method.getAnnotation(Permissions.class);
            permissions = new HashSet<>(Arrays.asList(permission.value()));
        }

        String label = interaction.value() + " " + command.value();
        while (label.contains("  ")) {
            label = label.replaceAll(" {2}", " ");
        }
        label = label.trim();

        if (label.isEmpty()) {
            logError("Labels must not be empty!", method);
            return Optional.empty();
        }

        // build parameter definitions
        List<ParameterDefinition> parameters = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            parameters.add(ParameterDefinition.build(parameter, validatorRegistry));
        }

        if (parameters.isEmpty()) {
            logError(String.format("First parameter must be of type %s!", CommandEvent.class.getSimpleName()), method);
            return Optional.empty();
        }

        // validate parameter definitions
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

            // argument parsing can be skipped by using just a String array (the traditional way of command frameworks)
            // this means that no other parameters are allowed in this case
            if (type.isAssignableFrom(String[].class) && parameters.size() > 2) {
                logError("Additional parameters aren't allowed when using arrays!", method);
                return Optional.empty();
            }
        }

        Set<net.dv8tion.jda.api.Permission> enabledFor = Arrays.stream(command.enabledFor()).collect(Collectors.toSet());
        if (enabledFor.size() == 1 && enabledFor.contains(net.dv8tion.jda.api.Permission.UNKNOWN)) {
            enabledFor.clear();
        }

        return Optional.of(new SlashCommandDefinition(
                method,
                command.ephemeral(),
                label,
                permissions,
                command.isGuildOnly(),
                command.isNSFW(),
                Command.Type.SLASH,
                enabledFor,
                command.scope(),
                localizationFunction,
                command.desc(),
                parameters,
                CooldownDefinition.build(method.getAnnotation(Cooldown.class)),
                false
        ));
    }

    private static void logError(String message, Method commandMethod) {
        log.error("An error has occurred! Skipping Command {}.{}:",
                commandMethod.getDeclaringClass().getSimpleName(),
                commandMethod.getName(),
                new IllegalArgumentException(message));
    }

    /**
     * Transforms this command definition to a {@link SlashCommandData}.
     *
     * @return the transformed {@link SlashCommandData}
     */
    @Override
    public SlashCommandData toCommandData() {
        SlashCommandData command = Commands.slash(
                name,
                description.replaceAll("N/A", "no description")
        );
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setLocalizationFunction(localizationFunction)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions));
        parameters.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.getType())) {
                return;
            }
            command.addOptions(parameter.toOptionData(isAutoComplete));
        });
        return command;
    }

    /**
     * Transforms this command definition to a {@link SubcommandData}.
     *
     * @param label the name of the sub command
     * @return the transformed {@link SubcommandData}
     */
    public SubcommandData toSubCommandData(String label) {
        SubcommandData command = new SubcommandData(
                label,
                description.replaceAll("N/A", "no description")

        );
        parameters.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.getType())) {
                return;
            }
            command.addOptions(parameter.toOptionData(isAutoComplete));
        });
        return command;
    }

    /**
     * Returns the slash command description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
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
     * @return a possibly-empty list of all {@link ParameterDefinition ParameterDefinitions} excluding the
     * {@link CommandEvent} at index 0
     */
    public List<ParameterDefinition> getActualParameters() {
        return parameters.subList(parameters.isEmpty() ? 0 : 1, parameters.size());
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
     * Whether this command has a cooldown. More formally, checks if {@link CooldownDefinition#getDelay()} > 0.
     *
     * @return {@code true} if this command has a cooldown.
     */
    public boolean hasCooldown() {
        return getCooldown().getDelay() > 0;
    }

    /**
     * Whether this command supports auto complete.
     *
     * @return {@code true} if this command supports auto complete
     */
    public boolean isAutoComplete() {
        return isAutoComplete;
    }

    /**
     * Whether this command supports auto complete.
     *
     * @param autoComplete whether this can command support auto complete
     * @return this instance for fluent interface
     */
    public SlashCommandDefinition setAutoComplete(boolean autoComplete) {
        this.isAutoComplete = autoComplete;
        return this;
    }

    @Override
    public String toString() {
        return "SlashCommandDefinition{" +
               "id='" + definitionId + '\'' +
               ", method=" + method +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", parameters=" + parameters +
               ", cooldown=" + cooldown +
               ", isAutoComplete=" + isAutoComplete +
               ", permissions=" + permissions +
               ", isGuildOnly=" + isGuildOnly +
               ", isNSFW=" + isNSFW +
               ", commandType=" + commandType +
               ", enabledPermissions=" + enabledPermissions +
               ", scope=" + scope +
               ", localizationFunction=" + localizationFunction +
               ", ephemeral=" + ephemeral +
               '}';
    }
}
