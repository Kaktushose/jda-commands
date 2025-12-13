package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getFramework;
import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getJdaEvent;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

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
        builder.setContent(getFramework().messageResolver().resolve(message, RichInvocationContext.getUserLocale(), placeholder));
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
        // this API only works for CV1 and underlying parts rely on no CV2 being present
        if (this.builder.isUsingComponentsV2()) {
            throw new IllegalArgumentException(JDACException.errorMessage("illegal-cv2-usage"));
        }
    }

    public Collection<ActionComponent> components() {
        return builder.getComponents().stream()
                .map(ActionRow.class::cast)
                .flatMap(it -> it.getComponents().stream())
                .map(ActionComponent.class::cast).toList();
    }

    public void addComponents(ActionRow... components) {
        builder.addComponents(components);
    }

    public void addEmbeds(MessageEmbed... embeds) {
        builder.addEmbeds(embeds);
    }

    public Message reply() {
        InteractionDefinition definition = RichInvocationContext.getInvocationContext().definition();

        GenericInteractionCreateEvent jdaEvent = getJdaEvent();
        switch (jdaEvent) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply ->
                    deferEdit(modalEvent);
            case IMessageEditCallback callback when editReply -> deferEdit(callback);
            case IReplyCallback callback -> deferReply(callback);
            default -> throw new InternalException("reply-failed", entry("getJdaEvent()", jdaEvent.getClass().getName()));
        }
        if (jdaEvent instanceof ModalInteractionEvent modalEvent) {
            editReply = modalEvent.getMessage() != null;
        }
        var hook = ((IDeferrableCallback) jdaEvent).getHook();

        log.debug(
                "Replying to interaction \"{}\" with content: {} [ephemeral={}, editReply={}, keepComponents={}, keepSelections={}]",
                definition.displayName(), builder.build().toData(), ephemeral, editReply, keepComponents, keepSelections
        );
        if (jdaEvent instanceof ComponentInteraction interaction && keepComponents) {
            builder.addComponents(retrieveComponents(interaction.getMessage()));
        }
        if (editReply) {
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build())).complete();
        }
        return hook.setEphemeral(ephemeral).sendMessage(builder.build()).complete();
    }

    private List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        MessageComponentTree componentTree = original.getComponentTree();

        if (!keepSelections) {
            return original.getComponents();
        }

        for (MessageTopLevelComponentUnion topLevel : componentTree.getComponents()) {
            for (ActionComponent oldComponent : topLevel.asActionRow().getActionComponents()) {
                ActionComponent newComponent = switch (oldComponent) {
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
                    default -> oldComponent;
                };

                componentTree = componentTree.replace(ComponentReplacer.byUniqueId(oldComponent, newComponent));
            }
        }
        return componentTree.getComponents();
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
