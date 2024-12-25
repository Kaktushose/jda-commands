package com.github.kaktushose.jda.commands.definitions.interactions.impl.command;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;

import static com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand.CommandScope;

public sealed class CommandDefinition implements Interaction, JDAEntity<CommandData> permits SlashCommandDefinition {

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

    @NotNull
    public ClassDescription clazz() {
        return clazz;
    }

    @NotNull
    public MethodDescription method() {
        return method;
    }

    @NotNull
    @Override
    public  SequencedCollection<Class<?>> methodSignature() {
        var type = switch (commandType) {
            case USER -> User.class;
            case MESSAGE -> Message.class;
            default -> throw new IllegalStateException("Unknown CommandType" + commandType);
        };
        return List.of(CommandEvent.class, type);
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
}
