package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.api.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.api.features.Replyable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.util.Set;

public non-sealed interface CommandDefinition extends JDAEntity<CommandData>, Replyable, PermissionsInteraction {

    String name();

    boolean isGuildOnly();

    boolean isNSFW();

    Command.Type commandType();

    Set<Permission> enabledPermissions();

    SlashCommand.CommandScope scope();

    LocalizationFunction localizationFunction();

}
