package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.dispatching.reply.Reply;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Subtype of [Event] that supports replying to the [GenericInteractionCreateEvent] with text messages.
///
/// You can either reply directly by using one of the `reply` methods, like [#reply(String)], or you can call
/// [#with()] to use a [ConfigurableReply] to append components or override reply settings from the
/// [`ReplyConfig`][com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig].
///
/// Example:
/// ```
/// public void onInteraction(ReplayableEvent<?> event) {
///     event.reply("Hello World");
/// }
/// ```
///
/// @since 4.0.0
/// @see ModalEvent
/// @see ModalReplyableEvent
/// @since 4.0.0
public sealed abstract class ReplyableEvent<T extends GenericInteractionCreateEvent> extends Event<T> implements Reply
        permits ModalEvent, ModalReplyableEvent {

    private static final Logger log = LoggerFactory.getLogger(ReplyableEvent.class);
    protected final EphemeralInteractionDefinition definition;
    private final ReplyConfig replyConfig;

    /// Constructs a new ReplyableEvent.
    ///
    /// @param event               the subtype [T] of [GenericInteractionCreateEvent]
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the [Runtime] this event lives in
    /// @param definition          the [EphemeralInteractionDefinition] this event belongs to
    protected ReplyableEvent(T event,
                             InteractionRegistry interactionRegistry,
                             Runtime runtime,
                             EphemeralInteractionDefinition definition) {
        super(event, interactionRegistry, runtime);
        this.replyConfig = definition.replyConfig();
        this.definition = definition;
    }

    /// Removes all components from the original message.
    ///
    /// The original message is the very first reply that was sent using the [#reply(String)] or its overloads.
    public void removeComponents() {
        log.debug("Reply Debug: Removing components from original message");
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(replyConfig.ephemeral()).queue();
            }
            callback.getHook().editOriginalComponents().queue();
        }
    }

    /// Entry point for configuring a reply.
    ///
    /// Returns a new [ConfigurableReply] that can be used to append components or override reply settings.
    ///
    /// @return [ConfigurableReply]
    /// @see [ConfigurableReply]
    @NotNull
    public ConfigurableReply with() {
        return new ConfigurableReply(newReply(), interactionRegistry, runtimeId());
    }

    @NotNull
    public Message reply(@NotNull String message) {
        return newReply().reply(message);
    }

    @NotNull
    public Message reply(@NotNull MessageCreateData message) {
        return newReply().reply(message);
    }

    @NotNull
    public Message reply(@NotNull EmbedBuilder builder) {
        return newReply().reply(builder);
    }

    private MessageReply newReply() {
        log.debug("Reply Debug: [Runtime={}]", runtimeId());
        return new MessageReply(event, definition);
    }
}
