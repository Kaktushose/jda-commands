package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.ContextCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
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

import static com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand.CommandScope;

public record ContextCommandDefinition(
        ClassDescription clazzDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        String name,
        Command.Type commandType,
        CommandScope scope,
        boolean guildOnly,
        boolean nsfw,
        Set<Permission> enabledPermissions,
        LocalizationFunction localizationFunction
) implements CommandDefinition {

    public static Optional<Definition> build(MethodBuildContext context) {
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

    @Override
    public String displayName() {
        return name;
    }
}
