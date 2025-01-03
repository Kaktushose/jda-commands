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
import com.github.kaktushose.jda.commands.dispatching.reply.GlobalReplyConfig;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

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
        return String.valueOf((clazzDescription().clazz().getName() + methodDescription().name()).hashCode());
    }

    /// Creates a new instance of the [Interaction] class.
    ///
    /// @return a new instance of the declaring class of the method this definition is bound to.
    /// @throws InvocationTargetException if the object creation fails
    /// @throws InstantiationException    if the object creation fails
    /// @throws IllegalAccessException    if the object creation fails
    default Object newInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazzDescription().clazz().getConstructors()[0].newInstance();
    }

    /// A possibly-empty [Collection] of permissions for this interaction.
    ///
    /// @apiNote The [PermissionsMiddleware] will validate the provided permissions.
    @NotNull
    Collection<String> permissions();

    /// The [ReplyConfig] that should be used when sending replies.
    ///
    /// @implNote This will first attempt to use the [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig]
    /// annotation of the method and then of the class. If neither is present will fall back to the [GlobalReplyConfig].
    @NotNull
    default ReplyConfig replyConfig() {
        var global = clazzDescription().annotation(com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig.class);
        var local = methodDescription().annotation(com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig.class);

        if (global.isEmpty() && local.isEmpty()) {
            return new ReplyConfig();
        }

        return local.map(ReplyConfig::new).orElseGet(() -> new ReplyConfig(global.get()));

    }

    /// Stores the configuration values for sending replies. This acts as a representation of
    /// [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig].
    ///
    /// @see [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig]
    record ReplyConfig(boolean ephemeral, boolean keepComponents, boolean editReply) {

        /// Constructs a new ReplyConfig using the default values specified by [GlobalReplyConfig].
        public ReplyConfig() {
            this(GlobalReplyConfig.ephemeral, GlobalReplyConfig.keepComponents, GlobalReplyConfig.editReply);
        }

        /// Constructs a new ReplyConfig.
        ///
        /// @param replyConfig the [com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig] to represent
        public ReplyConfig(@NotNull com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig replyConfig) {
            this(replyConfig.ephemeral(), replyConfig.keepComponents(), replyConfig.editReply());
        }
    }
}
