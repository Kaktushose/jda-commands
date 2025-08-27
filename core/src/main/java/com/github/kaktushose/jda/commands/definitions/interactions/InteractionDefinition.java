package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;

/// Common interface for all definitions that represent an interaction.
///
/// @see AutoCompleteDefinition
/// @see ModalDefinition
/// @see ContextCommandDefinition
/// @see SlashCommandDefinition
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
public sealed interface InteractionDefinition extends Definition, Invokable
        permits AutoCompleteDefinition, ModalDefinition, CommandDefinition, ComponentDefinition {

    /// The id for this definition. For interaction definition this is the hash code of the full class name and method
    /// name combined.
    @Override
    default String definitionId() {
        return createDefinitionId(classDescription().name(), methodDescription().name());
    }

    /// Creates a definition id from the classname and method name
    ///
    /// @param className the classname ([Class#getName()] or [ClassDescription#name()])
    /// @param methodName the method name ([Method#getName()] or [MethodDescription#name()])
    ///
    /// @return the definition id
    static String createDefinitionId(String className, String methodName) {
        return String.valueOf((className + methodName).hashCode());
    }

    /// A possibly-empty [Collection] of permissions for this interaction.
    ///
    /// @apiNote The [PermissionsMiddleware] will validate the provided permissions.
    
    Collection<String> permissions();

    /// Stores the configuration values for sending replies. This acts as a representation of
    /// [`ReplyConfig`][com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig].
    ///
    /// @see com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig ReplyConfig
    record ReplyConfig(boolean ephemeral, boolean keepComponents, boolean keepSelections, boolean editReply) {

        /// Constructs a new [ReplyConfig] using the following default values:
        /// - ephemeral: `false`
        /// - keepComponents: `true`
        /// - keepSelections: `true`
        /// - editReply: `true`
        public ReplyConfig() {
            this(false, true, true, true);
        }

        /// Constructs a new ReplyConfig after the given [Consumer] modified the [Builder].
        public static ReplyConfig of(Consumer<Builder> callback) {
            Builder builder = new Builder();
            callback.accept(builder);
            return builder.build();
        }

        /// Constructs a new ReplyConfig.
        ///
        /// @param replyConfig the [`ReplyConfig`][com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig] to represent
        public ReplyConfig(com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig replyConfig) {
            this(replyConfig.ephemeral(), replyConfig.keepComponents(), replyConfig.keepSelections(), replyConfig.editReply());
        }

        /// Builder for [ReplyConfig].
        public static class Builder {

            private boolean ephemeral;
            private boolean keepComponents;
            private boolean keepSelections;
            private boolean editReply;

            /// Constructs a new Builder.
            public Builder() {
                keepComponents = true;
                editReply = true;
            }

            /// Whether to send ephemeral replies. Default value is `false`.
            ///
            /// Ephemeral messages have some limitations and will be removed once the user restarts their client.
            /// Limitations:
            /// - Cannot contain any files/ attachments
            /// - Cannot be reacted to
            /// - Cannot be retrieved
            
            public Builder ephemeral(boolean ephemeral) {
                this.ephemeral = ephemeral;
                return this;
            }

            /// Whether to keep the original components when editing a message. Default value is `true`.
            ///
            /// More formally, if editing a message and `keepComponents` is `true`, the original message will first be queried and
            /// its components get added to the reply before it is sent.
            
            public Builder keepComponents(boolean keepComponents) {
                this.keepComponents = keepComponents;
                return this;
            }

            /// Whether to keep the selections of a string select menu when sending edits. This setting only has an effect with
            /// [#keepComponents()] `true`.
            
            public Builder keepSelections(boolean keepSelections) {
                this.keepSelections = keepSelections;
                return this;
            }

            /// Whether to edit the original message or to send a new one. Default value is `true`.
            ///
            /// The original message is the message, from which this event (interaction) originates.
            /// For example if this event is a ButtonEvent, the original message will be the message to which the pressed button is attached to.
            ///
            /// Subsequent replies to the same slash command event or the button event cannot be edited.
            
            public Builder editReply(boolean editReply) {
                this.editReply = editReply;
                return this;
            }

            private ReplyConfig build() {
                return new ReplyConfig(ephemeral, keepComponents, keepSelections, editReply);
            }
        }
    }
}
