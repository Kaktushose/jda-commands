package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.annotations.interactions.ContextCommand;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
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

/// Representation of a context command.
///
/// @param classDescription     the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription    the [MethodDescription] of the method this definition is bound to
/// @param permissions          a [Collection] of permissions for this command
/// @param name                 the name of the command
/// @param commandType          the [Command.Type] of this command
/// @param scope                the [CommandScope] of this command
/// @param guildOnly            whether this command can only be executed in guilds
/// @param nsfw                 whether this command is nsfw
/// @param enabledPermissions   a possibly-empty [Set] of [Permission]s this command will be enabled for
/// @param localizationFunction the [LocalizationFunction] to use for this command
public record ContextCommandDefinition(
        @NotNull ClassDescription classDescription,
        @NotNull MethodDescription methodDescription,
        @NotNull Collection<String> permissions,
        @NotNull String name,
        @NotNull Command.Type commandType,
        @NotNull CommandScope scope,
        boolean guildOnly,
        boolean nsfw,
        @NotNull Set<Permission> enabledPermissions,
        @NotNull LocalizationFunction localizationFunction
) implements CommandDefinition {

    /// Builds a new [ContextCommandDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [ContextCommandDefinition]
    @NotNull
    public static Optional<ContextCommandDefinition> build(MethodBuildContext context) {
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

        return Optional.of(new ContextCommandDefinition(
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

    /// Transforms this definition into [CommandData].
    ///
    /// @return the [CommandData]
    @NotNull
    @Override
    public CommandData toJDAEntity() {
        var command = Commands.context(commandType, name);
        command.setGuildOnly(guildOnly)
                .setNSFW(nsfw)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions))
                .setLocalizationFunction(localizationFunction);
        return command;
    }

    @NotNull
    @Override
    public String displayName() {
        return name;
    }
}
