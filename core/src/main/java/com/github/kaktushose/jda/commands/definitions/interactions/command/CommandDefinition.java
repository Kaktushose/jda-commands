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

    record CommandConfig(@NotNull InteractionContextType context, @NotNull IntegrationType integration, @NotNull CommandScope scope, boolean isNSFW) {

        public CommandConfig() {
            this(InteractionContextType.GUILD, IntegrationType.GUILD_INSTALL, CommandScope.GLOBAL, false);
        }

        public CommandConfig(com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig config) {
            this(config.context(), config.integration(), config.scope(), config.isNSFW());
        }

        public CommandConfig(Builder builder) {
            this(builder.context, builder.integration, builder.scope, builder.isNSFW);
        }

        public static class Builder {

            private InteractionContextType context;
            private IntegrationType integration;
            private CommandScope scope;
            private boolean isNSFW;

            public Builder() {
                context = InteractionContextType.GUILD;
                integration = IntegrationType.GUILD_INSTALL;
                scope = CommandScope.GLOBAL;
                isNSFW = false;
            }

            public Builder context(InteractionContextType context) {
                this.context = context;
                return this;
            }

            public Builder integration(IntegrationType integration) {
                this.integration = integration;
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
