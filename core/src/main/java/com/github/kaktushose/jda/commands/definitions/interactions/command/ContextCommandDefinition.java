package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/// Representation of a context command.
///
/// @param classDescription     the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription    the [MethodDescription] of the method this definition is bound to
/// @param permissions          a [Collection] of permissions for this command
/// @param name                 the name of the command
/// @param commandType          the [Command.Type] of this command
/// @param commandConfig        the [CommandConfig] to use
/// @param localizationFunction the [LocalizationFunction] to use for this command
public record ContextCommandDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String name,
        Command.Type commandType,
        CommandConfig commandConfig,
        LocalizationFunction localizationFunction
) implements CommandDefinition {

    /// Builds a new [ContextCommandDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [ContextCommandDefinition]
    
    public static ContextCommandDefinition build(MethodBuildContext context) {
        var method = context.method();
        var command = method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.Command.class).orElseThrow();

        var type = switch (command.type()) {
            case USER -> User.class;
            case MESSAGE -> Message.class;
            default -> null;
        };
        if (type == null) {
            throw new InvalidDeclarationException("invalid-context-command-type");
        }

        Helpers.checkSignature(method, List.of(CommandEvent.class, type));

        return new ContextCommandDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                command.value(),
                command.type(),
                Helpers.commandConfig(context),
                context.localizationFunction()
        );
    }

    /// Transforms this definition into [CommandData].
    ///
    /// @return the [CommandData]
    
    @Override
    public CommandData toJDAEntity() {
        var command = Commands.context(commandType, name);
        command.setIntegrationTypes(commandConfig.integration())
                .setContexts(commandConfig.context())
                .setNSFW(commandConfig.isNSFW())
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(commandConfig.enabledPermissions()))
                .setLocalizationFunction(localizationFunction);
        return command;
    }

    
    @Override
    public String displayName() {
        return name;
    }
}
