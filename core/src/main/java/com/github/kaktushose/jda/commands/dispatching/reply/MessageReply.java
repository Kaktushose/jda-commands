package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
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
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
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
public sealed class MessageReply implements Reply permits ConfigurableReply {

    protected static final Logger log = LoggerFactory.getLogger(MessageReply.class);
    protected final GenericInteractionCreateEvent event;
    protected final InteractionDefinition definition;
    protected final MessageCreateBuilder builder;
    protected boolean ephemeral;
    protected boolean editReply;
    protected boolean keepComponents;
    protected boolean keepSelections;

    /// Constructs a new MessageReply.
    ///
    /// @param event       the corresponding [GenericInteractionCreateEvent]
    /// @param definition  the corresponding [InteractionDefinition]. This is mostly needed by the [ConfigurableReply]
    /// @param replyConfig the [InteractionDefinition.ReplyConfig] to use
    public MessageReply(@NotNull GenericInteractionCreateEvent event,
                        @NotNull InteractionDefinition definition,
                        @NotNull InteractionDefinition.ReplyConfig replyConfig) {
        this.event = event;
        this.definition = definition;
        this.ephemeral = replyConfig.ephemeral();
        this.editReply = replyConfig.editReply();
        this.keepComponents = replyConfig.keepComponents();
        this.builder = new MessageCreateBuilder();
        this.keepSelections = replyConfig.keepSelections();
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
    }

    public Message reply(@NotNull String message) {
        builder.setContent(message);
        return complete();
    }

    public Message reply(@NotNull MessageCreateData message) {
        builder.applyData(message);
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
    /// If editing a message and `keepComponents` is `true`, queries the original message first and adds its components
    /// to the reply before sending it.
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
        log.debug(
                "Replying to interaction \"{}\" with content: {} [ephemeral={}, editReply={}, keepComponents={}]",
                definition.displayName(), builder.build().toData(), ephemeral, editReply, keepComponents
        );
        var hook = ((IDeferrableCallback) event).getHook();
        if (editReply) {
            if (keepComponents) {
                builder.addComponents(retrieveComponents(hook));
            }
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build())).complete();
        }
        return hook.setEphemeral(ephemeral).sendMessage(builder.build()).complete();
    }

    private List<LayoutComponent> retrieveComponents(InteractionHook hook) {
        List<LayoutComponent> components = hook.retrieveOriginal().complete().getComponents();

        if (!keepSelections) return components;

        for (LayoutComponent layoutComponent : components) {
            for (ActionComponent actionComponent : layoutComponent.getActionComponents()) {
                ActionComponent newComponent = switch (actionComponent) {
                    case StringSelectMenu selectMenu when event instanceof StringSelectInteractionEvent selectEvent -> selectMenu
                            .createCopy()
                            .setDefaultValues(selectEvent.getValues())
                            .build();

                    case EntitySelectMenu selectMenu when event instanceof EntitySelectInteractionEvent selectEvent -> {

                        Collection<EntitySelectMenu.DefaultValue> defaultValues = new HashSet<>();
                        Mentions mentions = selectEvent.getInteraction().getMentions();

                        defaultValues.addAll(mentions.getMembers().stream().map(EntitySelectMenu.DefaultValue::from).toList());
                        defaultValues.addAll(mentions.getChannels().stream().map(EntitySelectMenu.DefaultValue::from).toList());
                        defaultValues.addAll(mentions.getRoles().stream().map(EntitySelectMenu.DefaultValue::from).toList());

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
