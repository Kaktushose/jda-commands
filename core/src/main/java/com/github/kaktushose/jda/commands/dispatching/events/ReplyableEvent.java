package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.Reply;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.ReplyAction;
import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


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
/// @see ModalEvent
/// @see CommandEvent
/// @see ComponentEvent
public sealed abstract class ReplyableEvent<T extends GenericInteractionCreateEvent> extends Event<T> implements Reply
        permits ModalEvent, ModalReplyableEvent {

    private static final Logger log = LoggerFactory.getLogger(ReplyableEvent.class);
    protected final InteractionDefinition definition;
    private final InteractionDefinition.ReplyConfig replyConfig;
    private final Embeds embeds;

    /// Constructs a new ReplyableEvent.
    ///
    /// @param event               the subtype [T] of [GenericInteractionCreateEvent]
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the [Runtime] this event lives in
    /// @param definition          the [InteractionDefinition] this event belongs to
    /// @param embeds              the corresponding [Embeds]
    protected ReplyableEvent(T event,
                             InteractionRegistry interactionRegistry,
                             Runtime runtime,
                             InteractionDefinition definition,
                             InteractionDefinition.ReplyConfig replyConfig,
                             Embeds embeds) {
        super(event, interactionRegistry, runtime);
        this.replyConfig = replyConfig;
        this.definition = definition;
        this.embeds = embeds;
    }

    /// Acknowledge this interaction and defer the reply to a later time.
    ///
    /// This will send a `<Bot> is thinking...` message in chat that will be updated later. This will use the respective
    /// [InteractionDefinition.ReplyConfig] to set the ephemeral flag. If your initial deferred message is ephemeral it
    /// cannot be made non-ephemeral later. Use [#deferReply(boolean)] to override the [InteractionDefinition.ReplyConfig].
    ///
    /// **You only have 3 seconds to acknowledge an interaction!**
    ///
    /// When the acknowledgement is sent after the interaction expired, you will receive [ErrorResponse#UNKNOWN_INTERACTION].
    ///
    /// Use [#reply(String)] to reply directly.
    public void deferReply() {
        deferReply(replyConfig.ephemeral());
    }

    /// Acknowledge this interaction and defer the reply to a later time.
    ///
    /// This will send a `<Bot> is thinking...` message in chat that will be updated later. This will use the passed
    /// boolean to set the ephemeral flag. If your initial deferred message is ephemeral it
    /// cannot be made non-ephemeral later. Use [#deferReply()] to use the [InteractionDefinition.ReplyConfig] for
    /// the ephemeral flag.
    ///
    /// **You only have 3 seconds to acknowledge an interaction!**
    ///
    /// When the acknowledgement is sent after the interaction expired, you will receive [ErrorResponse#UNKNOWN_INTERACTION].
    ///
    /// Use [#reply(String)] to reply directly.
    ///
    /// @param ephemeral yes
    public abstract void deferReply(boolean ephemeral);

    /// Removes all components from the original message.
    ///
    /// The original message is the message, from which this event (interaction) originates. For example if this event is a ButtonEvent, the original message will be the message to which the pressed button is attached to.
    public void removeComponents() {
        log.debug("Reply Debug: Removing components from original message");
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(replyConfig.ephemeral()).queue();
            }
            callback.getHook().editOriginalComponents().queue();
        }
    }

    /// Gets a [`Button`][com.github.kaktushose.jda.commands.annotations.interactions.Button] based on the method name
    /// and transforms it into a JDA [Button].
    ///
    /// The button will be linked to the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    /// This may be useful if you want to send a component without using the framework.
    ///
    /// @param button the name of the button defining method
    /// @return the JDA [Button]
    public Button getButton(String button) {
        return getComponent(button, null, ButtonDefinition.class);
    }

    /// Gets a [`Button`][com.github.kaktushose.jda.commands.annotations.interactions.Button] based on the method name
    /// and the given class and transforms it into a JDA [Button].
    ///
    /// The button will be [`runtime`]({@docRoot}/index.html#runtime-concept-heading)-independent.
    /// This may be useful if you want to send a component without using the framework.
    ///
    /// @param origin the [Class] of the method
    /// @param button the name of the button defining method
    /// @return the JDA [Button]
    public Button getButton(Class<?> origin, String button) {
        return getComponent(button, null, ButtonDefinition.class);
    }

    /// Gets a [StringSelectMenu] or [EntitySelectMenu] based on the method name and transforms it into a JDA [SelectMenu].
    ///
    /// The select menu will be linked to the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). This may be useful if you want to send a component
    /// without using the framework.
    ///
    /// @param menu the name of the select menu
    /// @return the JDA [SelectMenu]
    public SelectMenu getSelectMenu(String menu) {
        return getComponent(menu, null, SelectMenuDefinition.class);
    }

    /// Gets a [StringSelectMenu] or [EntitySelectMenu] based on the method name and transforms it into a JDA [SelectMenu].
    ///
    /// The select menu will be [`runtime`]({@docRoot}/index.html#runtime-concept-heading)-independent.
    /// This may be useful if you want to send a component without using the framework.
    ///
    /// @param origin the [Class] of the method
    /// @param menu   the name of the select menu
    /// @return the JDA [SelectMenu]
    public SelectMenu getSelectMenu(Class<?> origin, String menu) {
        return getComponent(menu, origin, SelectMenuDefinition.class);
    }

    @SuppressWarnings("unchecked")
    private <C extends ActionComponent, E extends CustomIdJDAEntity<?>> C getComponent(String component, @Nullable Class<?> origin, Class<E> type) {
        var className = origin == null ? definition.classDescription().name() : origin.getName();
        var id = String.valueOf((className + component).hashCode());
        var definition = registry.find(type, false, it -> it.definitionId().equals(id));
        return (C) definition.toJDAEntity(new CustomId(runtimeId(), definition.definitionId()));
    }

    /// Gets an [Embed] based on the given name.
    ///
    /// Use [#findEmbed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    ///
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource)]
    public Embed embed(String name) {
        return embeds.get(name, event.getUserLocale().toLocale());
    }

    /// Gets an [Embed] based on the given name and wraps it in an [Optional].
    ///
    /// Use this instead of [#embed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    /// @return an [Optional] holding the [Embed] or an empty [Optional] if an [Embed] with the given name doesn't exist
    public Optional<Embed> findEmbed(String name) {
        if (!embeds.exists(name)) {
            return Optional.empty();
        }
        return Optional.of(embeds.get(name));
    }

    /// Entry point for configuring a reply.
    ///
    /// Returns a new [ConfigurableReply] that can be used to append components or override reply settings.
    ///
    /// @return a new [ConfigurableReply]
    /// @see ConfigurableReply
    public ConfigurableReply with() {
        return new ConfigurableReply(event, definition, i18n(), newReply(), embeds, registry, runtimeId());
    }

    /// {@inheritDoc}
    ///
    /// @param placeholder {@inheritDoc}
    /// @param message {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public Message reply(String message, I18n.Entry... placeholder) {
        return newReply().reply(message, placeholder);
    }

    /// {@inheritDoc}
    ///
    /// @param first {@inheritDoc}
    /// @param additional {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public Message reply(MessageEmbed first, MessageEmbed... additional) {
        return newReply().reply(first, additional);
    }

    /// {@inheritDoc}
    ///
    /// @param data {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public Message reply(MessageCreateData data) {
        return newReply().reply(data);
    }

    private ReplyAction newReply() {
        log.debug("Reply Debug: [Runtime={}]", runtimeId());
        return new ReplyAction(event, definition, i18n(), replyConfig);
    }
}
