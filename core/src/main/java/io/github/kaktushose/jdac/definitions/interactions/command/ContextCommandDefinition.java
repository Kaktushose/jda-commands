package io.github.kaktushose.jdac.definitions.interactions.command;

import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionContextType;
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
        var command = method.annotation(io.github.kaktushose.jdac.annotations.interactions.Command.class).orElseThrow();

        switch (command.type()) {
            case USER -> Helpers.checkSignatureUserContext(method);
            case MESSAGE -> Helpers.checkSignature(method, List.of(CommandEvent.class, Message.class));
            default ->  throw new InvalidDeclarationException("invalid-context-command-type");
        }

        CommandConfig commandConfig = Helpers.commandConfig(context);
        // enforce guild context if user context command has member
        if (method.parameters().getLast().type().equals(Member.class) && isInvalidContext(commandConfig.context())) {
            throw new InvalidDeclarationException("member-context-guild");
        }

        return new ContextCommandDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                command.value(),
                command.type(),
                commandConfig,
                context.localizationFunction()
        );
    }

    private static boolean isInvalidContext(InteractionContextType[] types) {
        if (types.length != 1) {
            return true;
        }
        return types[0] != InteractionContextType.GUILD;
    }

    /// Transforms this definition into [CommandData].
    ///
    /// @return the [CommandData]
    @Override
    public CommandData toJDAEntity(int counter) {
        try {
            var command = Commands.context(commandType, name);
            command.setContexts(commandConfig.context());
            command.setIntegrationTypes(commandConfig.integration())
                    .setNSFW(commandConfig.isNSFW())
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(commandConfig.enabledPermissions()))
                    .setLocalizationFunction(localizationFunction);
            return command;
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return name;
    }
}
