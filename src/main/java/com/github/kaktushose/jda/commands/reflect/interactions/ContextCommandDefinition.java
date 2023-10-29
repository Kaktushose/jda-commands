package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.ContextMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ContextCommandDefinition extends EphemeralInteraction {

    private final String name;
    private final Set<String> permissions;
    private final boolean isGuildOnly;
    private final boolean isNSFW;
    private final Command.Type commandType;
    private final Set<net.dv8tion.jda.api.Permission> enabledPermissions;
    private final SlashCommand.CommandScope scope;
    private final LocalizationFunction localizationFunction;

    protected ContextCommandDefinition(Method method,
                                       boolean ephemeral,
                                       String name,
                                       Set<String> permissions,
                                       boolean isGuildOnly,
                                       boolean isNSFW,
                                       Command.Type commandType,
                                       Set<Permission> enabledPermissions,
                                       SlashCommand.CommandScope scope,
                                       LocalizationFunction localizationFunction) {
        super(method, ephemeral);
        this.name = name;
        this.permissions = permissions;
        this.isGuildOnly = isGuildOnly;
        this.isNSFW = isNSFW;
        this.commandType = commandType;
        this.enabledPermissions = enabledPermissions;
        this.scope = scope;
        this.localizationFunction = localizationFunction;
    }

    public static Optional<ContextCommandDefinition> build(@NotNull Method method, @NotNull LocalizationFunction localizationFunction) {
        if (!method.isAnnotationPresent(ContextMenu.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        ContextMenu command = method.getAnnotation(ContextMenu.class);
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

        Set<net.dv8tion.jda.api.Permission> enabledFor = Arrays.stream(command.enabledFor()).collect(Collectors.toSet());
        if (enabledFor.size() == 1 && enabledFor.contains(net.dv8tion.jda.api.Permission.UNKNOWN)) {
            enabledFor.clear();
        }

        return Optional.of(new ContextCommandDefinition(
                method,
                command.ephemeral(),
                command.value(),
                permissions,
                command.isGuildOnly(),
                command.isNSFW(),
                command.type(),
                enabledFor,
                command.scope(),
                localizationFunction
                ));
    }

    public CommandData toCommandData() {
        CommandData command = Commands.context(commandType, name);
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions))
                .setLocalizationFunction(localizationFunction);
        return command;
    }

    public String getName() {
        return name;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean isGuildOnly() {
        return isGuildOnly;
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    public Command.Type getCommandType() {
        return commandType;
    }

    public Set<Permission> getEnabledPermissions() {
        return enabledPermissions;
    }

    public LocalizationFunction getLocalizationFunction() {
        return localizationFunction;
    }

    /**
     * Gets the {@link SlashCommand.CommandScope CommandScope} of this command.
     *
     * @return the {@link SlashCommand.CommandScope CommandScope} of this command
     */
    public SlashCommand.CommandScope getCommandScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "ContextMenuDefinition{" +
                "name='" + name + '\'' +
                ", permissions=" + permissions +
                ", isGuildOnly=" + isGuildOnly +
                ", isNSFW=" + isNSFW +
                ", commandType=" + commandType +
                ", enabledPermissions=" + enabledPermissions +
                ", scope=" + scope +
                ", localizationFunction=" + localizationFunction +
                ", id='" + id + '\'' +
                ", method=" + method +
                '}';
    }
}
