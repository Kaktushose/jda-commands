package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;

public record ContextCommandDefinition(
        @NotNull Method method,
        @NotNull Collection<String> permissions,
        @NotNull String name,
        boolean isGuildOnly,
        boolean isNSFW,
        @NotNull Command.Type commandType,
        @NotNull Set<Permission> enabledPermissions,
        @NotNull SlashCommand.CommandScope scope,
        @NotNull LocalizationFunction localizationFunction
) implements JDAEntity<CommandData>, Replyable, PermissionsInteraction {

    @NotNull
    @Override
    public CommandData toJDAEntity() {
        CommandData command = Commands.context(commandType, name);
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions))
                .setLocalizationFunction(localizationFunction);
        return command;
    }

    @NotNull
    @Override
    public String displayName() {
        return name;
    }

    @NotNull
    @Override
    public SequencedCollection<Class<?>> parameters() {
        var type = switch (commandType) {
            case USER -> User.class;
            case MESSAGE -> Message.class;
            default -> throw new IllegalStateException("Unknown CommandType" + commandType);
        };
        return List.of(CommandEvent.class, type);
    }
}
