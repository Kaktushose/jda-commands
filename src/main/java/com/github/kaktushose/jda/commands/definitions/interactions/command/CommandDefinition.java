package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/// Common interface for command interaction definitions.
///
/// @see SlashCommandDefinition
/// @see ContextCommandDefinition
public sealed interface CommandDefinition extends InteractionDefinition, JDAEntity<CommandData> permits ContextCommandDefinition, SlashCommandDefinition {

    /// The name of the command.
    @NotNull String name();

    /// Whether this command can only be executed in guilds.
    boolean guildOnly();

    /// Whether this command is nsfw.
    boolean nsfw();

    /// The [Command.Type] of this command.
    @NotNull Command.Type commandType();

    /// A possibly-empty [Set] of [Permission]s this command will be enabled for.
    @NotNull Set<Permission> enabledPermissions();

    /// The [CommandScope] of this command.
    @NotNull CommandScope scope();

    /// The [LocalizationFunction] to use for this command.
    @NotNull LocalizationFunction localizationFunction();
}
