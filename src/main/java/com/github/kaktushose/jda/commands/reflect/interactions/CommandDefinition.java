package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.CooldownDefinition;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
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
 * @author Kaktushose
 * @version 4.0.0
 * @see SlashCommand
 * @since 2.0.0
 */
public class CommandDefinition extends EphemeralInteraction implements Comparable<CommandDefinition> {

    private final String name;
    private final String description;
    private final List<ParameterDefinition> parameters;
    private final Set<String> permissions;
    private final Set<net.dv8tion.jda.api.Permission> enabledPermissions;
    private final CooldownDefinition cooldown;
    private final boolean isGuildOnly;
    private final boolean isNSFW;
    private final SlashCommand.CommandScope scope;
    private final LocalizationFunction localizationFunction;

    protected CommandDefinition(Method method,
                                boolean ephemeral,
                                String name,
                                String description,
                                List<ParameterDefinition> parameters,
                                Set<String> permissions,
                                Set<net.dv8tion.jda.api.Permission> enabledPermissions,
                                CooldownDefinition cooldown,
                                boolean isGuildOnly,
                                boolean isNSFW,
                                SlashCommand.CommandScope scope, LocalizationFunction localizationFunction) {
        super(method, ephemeral);
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.permissions = permissions;
        this.enabledPermissions = enabledPermissions;
        this.cooldown = cooldown;
        this.isGuildOnly = isGuildOnly;
        this.isNSFW = isNSFW;
        this.scope = scope;
        this.localizationFunction = localizationFunction;
    }


    /**
     * Builds a new CommandDefinition.
     *
     * @param method               the {@link Method} of the command
     * @param validatorRegistry    the corresponding {@link ValidatorRegistry}
     * @param localizationFunction the {@link LocalizationFunction} to use
     * @return an {@link Optional} holding the CommandDefinition
     */
    public static Optional<CommandDefinition> build(@NotNull Method method,
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

        if (parameters.size() < 1) {
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

        return Optional.of(new CommandDefinition(
                method,
                command.ephemeral(),
                label,
                command.desc(),
                parameters,
                permissions,
                enabledFor,
                CooldownDefinition.build(method.getAnnotation(Cooldown.class)),
                command.isGuildOnly(),
                command.isNSFW(),
                command.scope(),
                localizationFunction));
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
            command.addOptions(parameter.toOptionData());
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
            command.addOptions(parameter.toOptionData());
        });
        return command;
    }

    /**
     * Gets the command name.
     *
     * @return the command name
     */
    public String getName() {
        return name;
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
     * Gets a set of permission Strings.
     *
     * @return set of permission Strings
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Gets a set of Discord permission Strings this command will be enabled for by default.
     *
     * @return a set of Discord permission Strings this command will be enabled for by default
     */
    public Set<net.dv8tion.jda.api.Permission> getEnabledPermissions() {
        return enabledPermissions;
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
     * Whether this command can be executed inside direct messages.
     *
     * @return {@code true} if this command can be executed inside direct messages
     */
    public boolean isGuildOnly() {
        return isGuildOnly;
    }

    /**
     * Whether this command can only be executed in NSFW channels.
     *
     * @return {@code true} if this command can only be executed in NSFW channels
     */
    public boolean isNSFW() {
        return isNSFW;
    }

    /**
     * Gets the {@link SlashCommand.CommandScope CommandScope} of this command.
     *
     * @return the {@link SlashCommand.CommandScope CommandScope} of this command
     */
    public SlashCommand.CommandScope getCommandScope() {
        return scope;
    }

    /**
     * Gets the {@link Method} of the command.
     *
     * @return the {@link Method} of the command
     */
    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "SlashCommandDefinition{" +
                "name='" + name + '\'' +
                ", description=" + description +
                ", parameters=" + parameters +
                ", permissions=" + permissions +
                ", cooldown=" + cooldown +
                ", isDM=" + isGuildOnly +
                ", ephemeral=" + ephemeral +
                ", id='" + id + '\'' +
                ", method=" + method +
                '}';
    }

    @Override
    public int compareTo(@NotNull CommandDefinition command) {
        return name.compareTo(command.name);
    }
}
