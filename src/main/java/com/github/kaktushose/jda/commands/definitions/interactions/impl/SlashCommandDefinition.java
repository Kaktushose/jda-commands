package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

public record SlashCommandDefinition(
        Method method,
        Collection<String> permissions,
        boolean isNSFW,
        boolean isGuildOnly,
        Command.Type commandType,
        Set<Permission> enabledPermissions,
        SlashCommand.CommandScope scope,
        LocalizationFunction localizationFunction,
        String name,
        String description,
        SequencedCollection<ParameterDefinition> commandParameters,
        CooldownDefinition cooldown,
        boolean isAutoComplete
) implements JDAEntity<SlashCommandData>, Replyable, PermissionsInteraction {

    @NotNull
    @Override
    public SlashCommandData toJDAEntity() {
        SlashCommandData command = Commands.slash(
                name,
                description.replaceAll("N/A", "no description")
        );
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setLocalizationFunction(localizationFunction)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions));
        commandParameters.forEach(parameter -> {
            if (CommandEvent.class.isAssignableFrom(parameter.type())) {
                return;
            }
            command.addOptions(parameter.toJDAEntity());
        });
        return command;
    }

    public SubcommandData toSubCommandData(String label) {
        SubcommandData command = new SubcommandData(
                label,
                description.replaceAll("N/A", "no description")

        );
        commandParameters.forEach(parameter -> {
            command.addOptions(parameter.toJDAEntity());
        });
        return command;
    }

    @NotNull
    @Override
    public String displayName() {
        return "/%s".formatted(name);
    }

    @NotNull
    @Override
    public SequencedCollection<Class<?>> methodSignature() {
        List<Class<?>> parameters = new ArrayList<>();
        parameters.add(CommandEvent.class);
        commandParameters.forEach(it -> parameters.add(it.type()));
        return parameters;
    }

    public record CooldownDefinition(long delay, TimeUnit timeUnit) implements Definition {
        @Override
        public @NotNull String displayName() {
            return "Cooldown of %d %s".formatted(delay, timeUnit.name());
        }
    }
}
