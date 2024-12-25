package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.Set;

public record CommandDefinition(
        String displayName,
        Method method,
        SequencedCollection<Class<?>> parameters,
        Collection<String> permissions,
        ReplyConfig replyConfig,
        boolean isNSFW,
        Command.Type commandType,
        Set<Permission> enabledPermissions,
        SlashCommand.CommandScope scope,
        LocalizationFunction localizationFunction
) implements JDAEntity<CommandData>, Replyable, PermissionsInteraction {

    @Override
    public CommandData toJDAEntity() {
        return null;
    }

}
