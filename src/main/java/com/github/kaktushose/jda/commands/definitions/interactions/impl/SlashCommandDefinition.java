package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public record SlashCommandDefinition(
        String displayName,
        Method method,
        SequencedCollection<Class<?>> parameters,
        Collection<String> permissions,
        ReplyConfig replyConfig,
        boolean isNSFW,
        Command.Type commandType,
        Set<Permission> enabledPermissions,
        SlashCommand.CommandScope scope,
        LocalizationFunction localizationFunction,
        String description,
        SequencedCollection<ParameterDefinition> commandParameters,
        CooldownDefinition cooldown,
        boolean isAutoComplete
) implements JDAEntity<CommandData>, Replyable, PermissionsInteraction {

    @Override
    public CommandData toJDAEntity() {
        return null;
    }

    public record ParameterDefinition(
            OptionData toJDAEntity,
            Class<?> type,
            boolean optional,
            String defaultValue,
            boolean primitive,
            String name,
            String description,
            SequencedCollection<Command.Choice> choices,
            Collection<ConstraintDefinition> constraints
    ) implements Definition, JDAEntity<OptionData> {

        @Override
        public String displayName() {
            return "";
        }

        public record ConstraintDefinition(Validator validator, String message,
                                           Object annotation) implements Definition {
            @Override
            public String displayName() {
                return validator.getClass().getName();
            }
        }
    }

    public record CooldownDefinition(long delay, TimeUnit timeUnit) implements Definition {
        @Override
        public String displayName() {
            return "Cooldown of %d %s".formatted(delay, timeUnit.name());
        }
    }
}
