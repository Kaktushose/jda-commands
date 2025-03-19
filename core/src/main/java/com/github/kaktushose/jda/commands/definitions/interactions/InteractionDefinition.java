package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
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
    @NotNull
    @Override
    default String definitionId() {
        return String.valueOf((classDescription().clazz().getName() + methodDescription().name()).hashCode());
    }

    /// Creates a new instance of the [Interaction] class.
    ///
    /// @return a new instance of the declaring class of the method this definition is bound to.
    /// @throws InvocationTargetException if the object creation fails
    /// @throws InstantiationException    if the object creation fails
    /// @throws IllegalAccessException    if the object creation fails
    @ApiStatus.Internal
    default Object newInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return classDescription().clazz().getConstructors()[0].newInstance();
    }

    /// A possibly-empty [Collection] of permissions for this interaction.
    ///
    /// @apiNote The [PermissionsMiddleware] will validate the provided permissions.
    @NotNull
    Collection<String> permissions();

    /// Stores the configuration values for sending replies. This acts as a representation of
    /// [`ReplyConfig`][com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig].
    ///
    /// @see com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig ReplyConfig
    record ReplyConfig(boolean ephemeral, boolean keepComponents, boolean editReply) {

        /// Constructs a new [ReplyConfig] using the following default values:
        /// - ephemeral: `false`
        /// - keepComponents: `true`
        /// - editReply: `true`
        public ReplyConfig() {
            this(false, true, true);
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
        public ReplyConfig(@NotNull com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig replyConfig) {
            this(replyConfig.ephemeral(), replyConfig.keepComponents(), replyConfig.editReply());
        }

        /// Builder for [ReplyConfig].
        public static class Builder {

            private boolean ephemeral;
            private boolean keepComponents;
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
            @NotNull
            public Builder ephemeral(boolean ephemeral) {
                this.ephemeral = ephemeral;
                return this;
            }

            /// Whether to keep the original components when editing a message. Default value is `true`.
            ///
            /// More formally, if editing a message and `keepComponents` is `true`, the original message will first be queried and
            /// its components get added to the reply before it is sent.
            @NotNull
            public Builder keepComponents(boolean keepComponents) {
                this.keepComponents = keepComponents;
                return this;
            }

            /// Whether to edit the original message or to send a new one. Default value is `true`.
            ///
            /// The original message is the message, from which this event (interaction) originates.
            /// For example if this event is a ButtonEvent, the original message will be the message to which the pressed button is attached to.
            ///
            /// Subsequent replies to the same slash command event or the button event cannot be edited.
            @NotNull
            public Builder editReply(boolean editReply) {
                this.editReply = editReply;
                return this;
            }

            private ReplyConfig build() {
                return new ReplyConfig(ephemeral, keepComponents, editReply);
            }
        }
    }
}
