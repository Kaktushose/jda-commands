package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public sealed interface CommandDefinition extends InteractionDefinition, JDAEntity<CommandData> permits ContextCommandDefinition, SlashCommandDefinition {
    @NotNull String name();

    boolean guildOnly();

    boolean nsfw();

    @NotNull Command.Type commandType();

    @NotNull Set<Permission> enabledPermissions();

    @NotNull SlashCommand.CommandScope scope();

    @NotNull LocalizationFunction localizationFunction();
}
