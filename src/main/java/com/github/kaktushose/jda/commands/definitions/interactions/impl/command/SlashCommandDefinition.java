package com.github.kaktushose.jda.commands.definitions.interactions.impl.command;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.ParameterDefinition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class SlashCommandDefinition extends CommandDefinition {

    private final String description;
    private final SequencedCollection<ParameterDefinition> commandParameters;
    private final SlashCommandDefinition.CooldownDefinition cooldown;
    private final boolean isAutoComplete;

    public SlashCommandDefinition(@NotNull ClassDescription clazz,
                                  @NotNull MethodDescription method,
                                  @NotNull Collection<String> permissions,
                                  @NotNull String name,
                                  @NotNull SlashCommand.CommandScope scope,
                                  boolean isGuildOnly,
                                  boolean isNSFW,
                                  @NotNull Set<Permission> enabledPermissions,
                                  @NotNull LocalizationFunction localizationFunction,
                                  @NotNull String description,
                                  @NotNull SequencedCollection<ParameterDefinition> commandParameters,
                                  @NotNull CooldownDefinition cooldown,
                                  boolean isAutoComplete) {
        super(clazz, method, permissions, name, Type.SLASH, scope, isGuildOnly, isNSFW, enabledPermissions, localizationFunction);
        this.description = description;
        this.commandParameters = commandParameters;
        this.cooldown = cooldown;
        this.isAutoComplete = isAutoComplete;
    }

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

    public CooldownDefinition cooldown() {
        return cooldown;
    }

    public boolean autoComplete() {
        return isAutoComplete;
    }

    public record CooldownDefinition(long delay, TimeUnit timeUnit) implements Definition {
        @Override
        public @NotNull String displayName() {
            return "Cooldown of %d %s".formatted(delay, timeUnit.name());
        }
    }
}
