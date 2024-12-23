package com.github.kaktushose.jda.commands.definitions.reflect.interactions.commands;

import com.github.kaktushose.jda.commands.internal.Helpers;
import com.github.kaktushose.jda.commands.annotations.interactions.ContextCommand;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.definitions.reflect.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a ContextMenu command.
 *
 * @see ContextCommand
 * @since 4.0.0
 */
public final class ContextCommandDefinition extends GenericCommandDefinition {

    public ContextCommandDefinition(Method method,
                                    ReplyConfig replyConfig,
                                    String name,
                                    Set<String> permissions,
                                    boolean isGuildOnly,
                                    boolean isNSFW,
                                    Command.Type commandType,
                                    Set<Permission> enabledPermissions,
                                    SlashCommand.CommandScope scope,
                                    LocalizationFunction localizationFunction) {
        super(method, replyConfig, name, permissions, isGuildOnly, isNSFW, commandType, enabledPermissions, scope, localizationFunction);
    }

    public static Optional<ContextCommandDefinition> build(@NotNull MethodBuildContext context) {
        Method method = context.method();
        if (!method.isAnnotationPresent(ContextCommand.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterAmount(method, 2)) {
            return Optional.empty();
        }
        if (Helpers.isIncorrectParameterType(method, 0, CommandEvent.class)) {
            return Optional.empty();
        }
        if (Helpers.isIncorrectParameterType(method, 1, User.class) && Helpers.isIncorrectParameterType(method, 1, Message.class)) {
            return Optional.empty();
        }


        ContextCommand command = method.getAnnotation(ContextCommand.class);

        Set<net.dv8tion.jda.api.Permission> enabledFor = Arrays.stream(command.enabledFor()).collect(Collectors.toSet());
        if (enabledFor.size() == 1 && enabledFor.contains(net.dv8tion.jda.api.Permission.UNKNOWN)) {
            enabledFor.clear();
        }

        return Optional.of(new ContextCommandDefinition(
                method,
                Helpers.replyConfig(method),
                command.value(),
                Helpers.permissions(context),
                command.isGuildOnly(),
                command.isNSFW(),
                command.type(),
                enabledFor,
                command.scope(),
                context.localizationFunction()
        ));
    }

    @Override
    public CommandData toCommandData() {
        CommandData command = Commands.context(commandType, name);
        command.setGuildOnly(isGuildOnly)
                .setNSFW(isNSFW)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions))
                .setLocalizationFunction(localizationFunction);
        return command;
    }

    @Override
    public String toString() {
        return "ContextCommandDefinition{" +
                "id='" + definitionId + '\'' +
                ", method=" + method +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                ", isGuildOnly=" + isGuildOnly +
                ", isNSFW=" + isNSFW +
                ", commandType=" + commandType +
                ", enabledPermissions=" + enabledPermissions +
                ", scope=" + scope +
                ", localizationFunction=" + localizationFunction +
                ", replyConfig=" + replyConfig +
                '}';
    }
}
