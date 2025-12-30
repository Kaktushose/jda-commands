package io.github.kaktushose.jdac.definitions.interactions.command;

import io.github.kaktushose.jdac.annotations.interactions.CommandScope;
import io.github.kaktushose.jdac.definitions.features.NonCustomIdJDAEntity;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/// Common interface for command interaction definitions.
///
/// @see SlashCommandDefinition
/// @see ContextCommandDefinition
public sealed interface CommandDefinition extends InteractionDefinition, NonCustomIdJDAEntity<CommandData> permits ContextCommandDefinition, SlashCommandDefinition {

    /// The name of the command.
    String name();

    CommandConfig commandConfig();

    /// The [Command.Type] of this command.
    Command.Type commandType();

    /// The [LocalizationFunction] to use for this command.
    LocalizationFunction localizationFunction();

    /// Stores the configuration values for registering commands. This acts as a representation of
    /// [io.github.kaktushose.jdac.annotations.interactions.CommandConfig]
    ///
    /// @see io.github.kaktushose.jdac.annotations.interactions.CommandConfig
    record CommandConfig(InteractionContextType[] context, IntegrationType[] integration,
                         CommandScope scope, boolean isNSFW, Permission[] enabledPermissions) {

        /// Compact constructor ensuring that [#context] and [#integration] is always set. If empty defaults to
        /// [InteractionContextType#GUILD] and [IntegrationType#GUILD_INSTALL].
        public CommandConfig {
            if (context.length == 0) {
                context = new InteractionContextType[]{InteractionContextType.GUILD};
            }
            if (integration.length == 0) {
                integration = new IntegrationType[]{IntegrationType.GUILD_INSTALL};
            }
            // Permission.UNKNOWN is the default value in the @CommandConfig annotation indicating DefaultMemberPermissions.ENABLED
            // which must be represented as an empty array
            if (enabledPermissions.length == 1 && enabledPermissions[0] == Permission.UNKNOWN) {
                enabledPermissions = new Permission[0];
            }
        }

        /// Constructs a new CommandConfig using the following default values:
        /// - [InteractionContextType#GUILD]
        /// - [IntegrationType#GUILD_INSTALL]
        /// - [CommandScope#GLOBAL]
        /// - isNSFW: `false`
        public CommandConfig() {
            this(new InteractionContextType[0], new IntegrationType[0], CommandScope.GLOBAL, false, new Permission[0]);
        }

        /// Constructs a new CommandConfig.
        ///
        /// @param config the [`@CommandConfig`][io.github.kaktushose.jdac.annotations.interactions.CommandConfig] to represent
        public CommandConfig(io.github.kaktushose.jdac.annotations.interactions.CommandConfig config) {
            this(config.context(), config.integration(), config.scope(), config.isNSFW(), config.enabledFor());
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
            private final Set<Permission> enabledPermissions;
            private CommandScope scope;
            private boolean isNSFW;

            /// Constructs a new Builder.
            public Builder() {
                context = new HashSet<>();
                integration = new HashSet<>();
                scope = CommandScope.GLOBAL;
                isNSFW = false;
                enabledPermissions = new HashSet<>();
            }

            /// The [InteractionContextType]s to use. This method will override this builders default
            /// value [InteractionContextType#GUILD].
            ///
            /// @param context the [InteractionContextType]s to use
            public Builder context(InteractionContextType... context) {
                return context(Arrays.asList(context));
            }

            /// The [InteractionContextType]s to use. This method will override this builders default
            /// value [InteractionContextType#GUILD].
            ///
            /// @param context the [InteractionContextType]s to use
            public Builder context(Collection<InteractionContextType> context) {
                this.context.addAll(context);
                return this;
            }

            /// The [IntegrationType]s to use. This method will override this builders default
            /// value [IntegrationType#GUILD_INSTALL].
            ///
            /// @param integration the [IntegrationType]s to use
            public Builder integration(IntegrationType... integration) {
                return integration(Arrays.asList(integration));
            }

            /// The [IntegrationType]s to use. This method will override this builders default
            /// value [IntegrationType#GUILD_INSTALL].
            ///
            /// @param integration the [IntegrationType]s to use
            public Builder integration(Collection<IntegrationType> integration) {
                this.integration.addAll(integration);
                return this;
            }

            /// @param scope the [CommandScope] to use
            public Builder scope(CommandScope scope) {
                this.scope = scope;
                return this;
            }

            /// @param nsfw `true` if the configured command(s) can only be executed in NSFW channels
            public Builder nsfw(boolean nsfw) {
                isNSFW = nsfw;
                return this;
            }

            /// @param permissions The default [Permission]s the configured command(s) will be enabled for.
            public Builder enabledPermissions(Permission... permissions) {
                return enabledPermissions(Arrays.asList(permissions));
            }

            /// @param permissions The default [Permission]s the configured command(s) will be enabled for.
            public Builder enabledPermissions(Collection<Permission> permissions) {
                enabledPermissions.addAll(permissions);
                return this;
            }

            private CommandConfig build() {
                return new CommandConfig(
                        context.toArray(InteractionContextType[]::new),
                        integration.toArray(IntegrationType[]::new),
                        scope,
                        isNSFW,
                        enabledPermissions.toArray(Permission[]::new)
                );
            }
        }
    }
}
