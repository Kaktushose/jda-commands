package com.github.kaktushose.jda.commands.definitions.interactions.command;

import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/// Common interface for command interaction definitions.
///
/// @see SlashCommandDefinition
/// @see ContextCommandDefinition
public sealed interface CommandDefinition extends InteractionDefinition, JDAEntity<CommandData> permits ContextCommandDefinition, SlashCommandDefinition {

    /// The name of the command.
    @NotNull String name();

    @NotNull CommandConfig commandConfig();

    /// The [Command.Type] of this command.
    @NotNull Command.Type commandType();

    /// A possibly-empty [Set] of [Permission]s this command will be enabled for.
    @NotNull Set<Permission> enabledPermissions();

    /// The [LocalizationFunction] to use for this command.
    @NotNull LocalizationFunction localizationFunction();

    record CommandConfig(@NotNull InteractionContextType[] context, @NotNull IntegrationType[] integration, @NotNull CommandScope scope, boolean isNSFW) {

        public CommandConfig {
            if (context.length == 0) {
                context = new InteractionContextType[]{InteractionContextType.GUILD};
            }
            if (integration.length == 0) {
                integration = new IntegrationType[]{IntegrationType.GUILD_INSTALL};
            }
        }

        public CommandConfig() {
            this(new InteractionContextType[0], new IntegrationType[0], CommandScope.GLOBAL, false);
        }

        public CommandConfig(com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig config) {
            this(config.context(), config.integration(), config.scope(), config.isNSFW());
        }

        public CommandConfig(Builder builder) {
            this(
                    builder.context.toArray(InteractionContextType[]::new),
                    builder.integration.toArray(IntegrationType[]::new),
                    builder.scope,
                    builder.isNSFW
            );
        }

        public static class Builder {

            private final Set<InteractionContextType> context;
            private final Set<IntegrationType> integration;
            private CommandScope scope;
            private boolean isNSFW;

            public Builder() {
                context = new HashSet<>();
                integration = new HashSet<>();
                scope = CommandScope.GLOBAL;
                isNSFW = false;
            }

            public Builder context(InteractionContextType context) {
                this.context.add(context);
                return this;
            }

            public Builder integration(IntegrationType integration) {
                this.integration.add(integration);
                return this;
            }

            public Builder scope(CommandScope scope) {
                this.scope = scope;
                return this;
            }

            public Builder nsfw(boolean NSFW) {
                isNSFW = NSFW;
                return this;
            }
        }
    }
}
