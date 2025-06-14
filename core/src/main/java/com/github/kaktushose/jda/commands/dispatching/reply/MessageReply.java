package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.MessageCreateDataReply;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/// Simple builder for sending text messages based on a [GenericInteractionCreateEvent].
///
/// More formally, can be used to
/// send an arbitrary amount of message replies to the text channel the [GenericInteractionCreateEvent] was executed in.
///
/// Example:
/// ```
/// new MessageReply(event, definition, new ReplyConfig()).reply(errorMessage);
///```
///
/// @see ConfigurableReply
/// @see ReplyableEvent
public sealed class MessageReply implements Reply permits ConfigurableReply, MessageCreateDataReply {

    protected static final Logger log = LoggerFactory.getLogger(MessageReply.class);
    protected final GenericInteractionCreateEvent event;
    protected final InteractionDefinition definition;
    protected final MessageCreateBuilder builder;
    protected final I18n i18n;
    protected boolean ephemeral;
    protected boolean editReply;
    protected boolean keepComponents;
    protected boolean keepSelections;

    /// Constructs a new MessageReply.
    ///
    /// @param event       the corresponding [GenericInteractionCreateEvent]
    /// @param definition  the corresponding [InteractionDefinition]. This is mostly needed by the [ConfigurableReply]
    /// @param i18n the [I18n] instance to use for localization
    /// @param replyConfig the [InteractionDefinition.ReplyConfig] to use
    public MessageReply(@NotNull GenericInteractionCreateEvent event,
                        @NotNull InteractionDefinition definition,
                        @NotNull I18n i18n,
                        @NotNull InteractionDefinition.ReplyConfig replyConfig) {
        this.event = event;
        this.definition = definition;
        this.i18n = i18n;
        this.ephemeral = replyConfig.ephemeral();
        this.editReply = replyConfig.editReply();
        this.keepComponents = replyConfig.keepComponents();
        this.keepSelections = replyConfig.keepSelections();
        this.builder = new MessageCreateBuilder();
    }

    /// Constructs a new MessageReply.
    ///
    /// @param reply the [MessageReply] to copy
    public MessageReply(@NotNull MessageReply reply) {
        this.event = reply.event;
        this.builder = reply.builder;
        this.definition = reply.definition;
        this.ephemeral = reply.ephemeral;
        this.editReply = reply.editReply;
        this.keepComponents = reply.keepComponents;
        this.keepSelections = reply.keepSelections;
        this.i18n = reply.i18n;
    }

    public Message reply(@NotNull String message, I18n.Entry... placeholder) {
        builder.setContent(i18n.localize(event.getUserLocale().toLocale(), message, placeholder));
        return complete();
    }

    public Message reply(@NotNull EmbedBuilder builder) {
        this.builder.setEmbeds(builder.build());
        return complete();
    }

    /// Sends the reply to Discord and blocks the current thread until the message was sent.
    ///
    /// @return the [Message] that got created
    /// @implNote This method can handle both message replies and message edits. it will check if the interaction got
    /// acknowledged and will acknowledge it if necessary before sending or editing a message. After that,
    /// [InteractionHook#sendMessage(MessageCreateData)] or respectively [InteractionHook#editOriginal(MessageEditData)]
    /// will be called.
    ///
    /// If `keepComponents` is `true`, queries the original message first and adds its components to the reply before sending it.
    @NotNull
    protected Message complete() {
        switch (event) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply ->
                    deferEdit(modalEvent);
            case IMessageEditCallback callback when editReply -> deferEdit(callback);
            case IReplyCallback callback -> deferReply(callback);
            default -> throw new IllegalArgumentException(
                    "Cannot reply to '%s'! Please report this error to the devs of jda-commands!".formatted(event.getClass().getName())
            );
        }
        if (event instanceof ModalInteractionEvent modalEvent) {
            editReply = modalEvent.getMessage() != null;
        }
        var hook = ((IDeferrableCallback) event).getHook();

        log.debug(
                "Replying to interaction \"{}\" with content: {} [ephemeral={}, editReply={}, keepComponents={}, keepSelections={}]",
                definition.displayName(), builder.build().toData(), ephemeral, editReply, keepComponents, keepSelections
        );
        if (event instanceof ComponentInteraction interaction && keepComponents) {
            builder.addComponents(retrieveComponents(interaction.getMessage()));
        }
        if (editReply) {
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build())).complete();
        }
        return hook.setEphemeral(ephemeral).sendMessage(builder.build()).complete();
    }

    private List<LayoutComponent> retrieveComponents(Message original) {
        List<LayoutComponent> components = original.getComponents();

        if (!keepSelections) {
            return components;
        }

        for (LayoutComponent layoutComponent : components) {
            for (ActionComponent actionComponent : layoutComponent.getActionComponents()) {
                ActionComponent newComponent = switch (actionComponent) {
                    case StringSelectMenu selectMenu when event instanceof StringSelectInteractionEvent selectEvent -> selectMenu
                            .createCopy()
                            .setDefaultValues(selectEvent.getValues())
                            .build();

                    case EntitySelectMenu selectMenu when event instanceof EntitySelectInteractionEvent selectEvent -> {

                        Collection<DefaultValue> defaultValues = new HashSet<>();
                        Mentions mentions = selectEvent.getInteraction().getMentions();

                        defaultValues.addAll(mentions.getMembers().stream().map(DefaultValue::from).toList());
                        defaultValues.addAll(mentions.getChannels().stream().map(DefaultValue::from).toList());
                        defaultValues.addAll(mentions.getRoles().stream().map(DefaultValue::from).toList());

                        yield selectMenu
                                .createCopy()
                                .setDefaultValues(defaultValues)
                                .build();
                    }

                    default -> actionComponent;
                };

                layoutComponent.updateComponent(actionComponent, newComponent);
            }
        }
        return components;
    }

    private void deferReply(@NotNull IReplyCallback callback) {
        if (!event.isAcknowledged()) {
            callback.deferReply(ephemeral).queue();
        }
    }

    private void deferEdit(@NotNull IMessageEditCallback callback) {
        if (!event.isAcknowledged()) {
            callback.deferEdit().queue();
        }
    }
}
