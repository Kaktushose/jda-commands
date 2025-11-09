package com.github.kaktushose.jda.commands.dispatching.reply.internal;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.internal.RichInvocationContext;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.kaktushose.jda.commands.dispatching.context.internal.RichInvocationContext.getJdaEvent;
import static com.github.kaktushose.jda.commands.message.placeholder.Entry.entry;

/// Implementation of [Reply] handling all the business logic of sending messages.
@ApiStatus.Internal
public final class ReplyAction implements Reply {

    private static final Logger log = LoggerFactory.getLogger(ReplyAction.class);

    private MessageCreateBuilder builder;
    private boolean ephemeral;
    private boolean keepComponents;
    private boolean keepSelections;
    private boolean editReply;

    /// Constructs a new ReplyAction.
    ///
    /// @param replyConfig the [InteractionDefinition.ReplyConfig] to use
    public ReplyAction(InteractionDefinition.ReplyConfig replyConfig) {
        this.ephemeral = replyConfig.ephemeral();
        this.editReply = replyConfig.editReply();
        this.keepComponents = replyConfig.keepComponents();
        this.keepSelections = replyConfig.keepSelections();
        this.builder = new MessageCreateBuilder();
    }

    @Override
    public Message reply(String message, Entry... placeholder) {
        builder.setContent(RichInvocationContext.getHolyGrail().messageResolver().resolve(message, RichInvocationContext.getUserLocale(), placeholder));
        return reply();
    }

    @Override
    public Message reply(MessageEmbed first, MessageEmbed... additional) {
        builder.setEmbeds(Stream.concat(Stream.of(first), Arrays.stream(additional)).toList());
        return reply();
    }

    @Override
    public Message reply(MessageCreateData data) {
        builder = MessageCreateBuilder.from(data);
        return reply();
    }

    public void ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public void keepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
    }

    public void keepSelections(boolean keepSelections) {
        this.keepSelections = keepSelections;
    }

    public void editReply(boolean editReply) {
        this.editReply = editReply;
    }

    public void builder(Consumer<MessageCreateBuilder> builder) {
        builder.accept(this.builder);
    }

    public Collection<LayoutComponent> components() {
        return List.copyOf(builder.getComponents());
    }

    public void addComponents(LayoutComponent... components) {
        builder.addComponents(components);
    }

    public void addEmbeds(MessageEmbed... embeds) {
        builder.addEmbeds(embeds);
    }

    public Message reply() {
        InteractionDefinition definition = RichInvocationContext.getInvocationContext().definition();

        switch (getJdaEvent()) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply ->
                    deferEdit(modalEvent);
            case IMessageEditCallback callback when editReply -> deferEdit(callback);
            case IReplyCallback callback -> deferReply(callback);
            default -> throw new InternalException("reply-failed", entry("getJdaEvent()", getJdaEvent().getClass().getName()));
        }
        if (getJdaEvent() instanceof ModalInteractionEvent modalEvent) {
            editReply = modalEvent.getMessage() != null;
        }
        var hook = ((IDeferrableCallback) getJdaEvent()).getHook();

        log.debug(
                "Replying to interaction \"{}\" with content: {} [ephemeral={}, editReply={}, keepComponents={}, keepSelections={}]",
                definition.displayName(), builder.build().toData(), ephemeral, editReply, keepComponents, keepSelections
        );
        if (getJdaEvent() instanceof ComponentInteraction interaction && keepComponents) {
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
                    case StringSelectMenu selectMenu when getJdaEvent() instanceof StringSelectInteractionEvent selectEvent -> selectMenu
                            .createCopy()
                            .setDefaultValues(selectEvent.getValues())
                            .build();

                    case EntitySelectMenu selectMenu when getJdaEvent() instanceof EntitySelectInteractionEvent selectEvent -> {

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

    private void deferReply(IReplyCallback callback) {
        if (!getJdaEvent().isAcknowledged()) {
            callback.deferReply(ephemeral).queue();
        }
    }

    private void deferEdit(IMessageEditCallback callback) {
        if (!getJdaEvent().isAcknowledged()) {
            callback.deferEdit().queue();
        }
    }
}
