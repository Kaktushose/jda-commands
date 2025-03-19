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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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

    /// Stores the configuration values for registering commands. This acts as a representation of
    /// [com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig]
    ///
    /// @see com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig
    record CommandConfig(@NotNull InteractionContextType[] context, @NotNull IntegrationType[] integration,
                         @NotNull CommandScope scope, boolean isNSFW) {

        /// Compact constructor ensuring that [#context] and [#integration] is always set. If empty defaults to
        /// [InteractionContextType#GUILD] and [IntegrationType#GUILD_INSTALL].
        public CommandConfig {
            if (context.length == 0) {
                context = new InteractionContextType[]{InteractionContextType.GUILD};
            }
            if (integration.length == 0) {
                integration = new IntegrationType[]{IntegrationType.GUILD_INSTALL};
            }
        }

        /// Constructs a new CommandConfig using the following default values:
        /// - [InteractionContextType#GUILD]
        /// - [IntegrationType#GUILD_INSTALL]
        /// - [CommandScope#GLOBAL]
        /// - isNSFW: `false`
        public CommandConfig() {
            this(new InteractionContextType[0], new IntegrationType[0], CommandScope.GLOBAL, false);
        }

        /// Constructs a new CommandConfig.
        ///
        /// @param config the [`@CommandConfig`][com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig] to represent
        public CommandConfig(com.github.kaktushose.jda.commands.annotations.interactions.CommandConfig config) {
            this(config.context(), config.integration(), config.scope(), config.isNSFW());
        }

        /// Constructs a new CommandConfig after the given [Consumer] modified the [Builder].
        public static CommandConfig of(Consumer<Builder> callback) {
            Builder builder = new Builder();
            callback.accept(builder);
            return builder.build();
        }

        /// Builder for [CommandConfig].
        public static class Builder {

            private final Set<InteractionContextType> context;
            private final Set<IntegrationType> integration;
            private CommandScope scope;
            private boolean isNSFW;

            /// Constructs a new Builder.
            public Builder() {
                context = new HashSet<>();
                integration = new HashSet<>();
                scope = CommandScope.GLOBAL;
                isNSFW = false;
            }

            /// The [InteractionContextType]s to use. This method will override this builders default
            /// value [InteractionContextType#GUILD].
            ///
            /// @param context the [InteractionContextType]s to use
            @NotNull
            public Builder context(@NotNull InteractionContextType... context) {
                this.context.addAll(Arrays.asList(context));
                return this;
            }

            /// The [IntegrationType]s to use. This method will override this builders default
            /// value [IntegrationType#GUILD_INSTALL].
            ///
            /// @param integration the [IntegrationType]s to use
            @NotNull
            public Builder integration(@NotNull IntegrationType... integration) {
                this.integration.addAll(Arrays.asList(integration));
                return this;
            }

            /// @param scope the [CommandScope] to use
            @NotNull
            public Builder scope(@NotNull CommandScope scope) {
                this.scope = scope;
                return this;
            }

            /// @param nsfw `true` if the configured command(s) can only be executed in NSFW channels
            @NotNull
            public Builder nsfw(boolean nsfw) {
                isNSFW = nsfw;
                return this;
            }

            private CommandConfig build() {
                return new CommandConfig(
                        context.toArray(InteractionContextType[]::new),
                        integration.toArray(IntegrationType[]::new),
                        scope,
                        isNSFW
                );
            }
        }
    }
}
