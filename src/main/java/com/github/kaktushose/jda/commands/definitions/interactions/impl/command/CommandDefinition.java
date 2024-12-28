package com.github.kaktushose.jda.commands.definitions.interactions.impl.command;

import com.github.kaktushose.jda.commands.annotations.interactions.ContextCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand.CommandScope;

public sealed class CommandDefinition implements InteractionDefinition, JDAEntity<CommandData> permits SlashCommandDefinition {

    protected final ClassDescription clazz;

    protected final MethodDescription method;
    protected final Collection<String> permissions;
    protected final String name;
    protected final boolean isGuildOnly;
    protected final boolean isNSFW;
    protected final Command.Type commandType;
    protected final Set<Permission> enabledPermissions;
    protected final CommandScope scope;
    protected final LocalizationFunction localizationFunction;
    public CommandDefinition(@NotNull ClassDescription clazz,
                             @NotNull MethodDescription method,
                             @NotNull Collection<String> permissions,
                             @NotNull String name,
                             @NotNull Command.Type commandType,
                             @NotNull CommandScope scope,
                             boolean isGuildOnly,
                             boolean isNSFW,
                             @NotNull Set<Permission> enabledPermissions,
                             @NotNull LocalizationFunction localizationFunction) {
        this.clazz = clazz;
        this.method = method;
        this.permissions = permissions;
        this.name = name;
        this.isGuildOnly = isGuildOnly;
        this.isNSFW = isNSFW;
        this.commandType = commandType;
        this.enabledPermissions = enabledPermissions;
        this.scope = scope;
        this.localizationFunction = localizationFunction;
    }

    public static Optional<Definition> build(MethodBuildContext context) {
        var method = context.method();
        ContextCommand command = method.annotation(ContextCommand.class).orElseThrow();

        var type = switch (command.type()) {
            case USER -> User.class;
            case MESSAGE -> Message.class;
            default -> null;
        };
        if (type == null) {
            log.error("Invalid command type for context command! Must either be USER or MESSAGE");
            return Optional.empty();
        }
        if (Helpers.checkSignature(method, List.of(CommandEvent.class, type))) {
            return Optional.empty();
        }

        Set<Permission> enabledFor = Arrays.stream(command.enabledFor()).collect(Collectors.toSet());
        if (enabledFor.size() == 1 && enabledFor.contains(Permission.UNKNOWN)) {
            enabledFor.clear();
        }

        return Optional.of(new CommandDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                command.value(),
                command.type(),
                command.scope(),
                command.isGuildOnly(),
                command.isNSFW(),
                enabledFor,
                context.localizationFunction()
        ));
    }

    @NotNull
    @Override
    public CommandData toJDAEntity() {
        var command = Commands.context(commandType, name);
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions))
                .setLocalizationFunction(localizationFunction);
        return command;
    }

    @Override
    public @NotNull ClassDescription clazz() {
        return clazz;
    }

    @NotNull
    public MethodDescription method() {
        return method;
    }

    @NotNull
    public Collection<String> permissions() {
        return permissions;
    }

    @NotNull
    public String name() {
        return name;
    }

    public boolean guildOnly() {
        return isGuildOnly;
    }

    public boolean nsfw() {
        return isNSFW;
    }

    @NotNull
    public Command.Type commandType() {
        return commandType;
    }

    @NotNull
    public Set<Permission> enabledPermissions() {
        return enabledPermissions;
    }

    @NotNull
    public CommandScope scope() {
        return scope;
    }

    @NotNull
    public LocalizationFunction localizationFunction() {
        return localizationFunction;
    }

    @Override
    public String displayName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommandDefinition that = (CommandDefinition) o;
        return isGuildOnly == that.isGuildOnly && isNSFW == that.isNSFW && Objects.equals(clazz, that.clazz) && Objects.equals(method, that.method) && Objects.equals(permissions, that.permissions) && Objects.equals(name, that.name) && commandType == that.commandType && Objects.equals(enabledPermissions, that.enabledPermissions) && scope == that.scope && Objects.equals(localizationFunction, that.localizationFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, method, permissions, name, isGuildOnly, isNSFW, commandType, enabledPermissions, scope, localizationFunction);
    }
}
