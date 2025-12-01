package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.exceptions.InternalException;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getInvocationContext;
import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getJdaEvent;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public abstract sealed class ReplyAction permits MessageReplyAction, ComponentReplyAction {

    private static final Logger log = LoggerFactory.getLogger(ReplyAction.class);
    protected MessageCreateBuilder builder;
    protected boolean ephemeral;
    protected boolean editReply;
    protected boolean keepComponents;
    protected boolean keepSelections;

    public ReplyAction(ReplyConfig replyConfig, MessageCreateBuilder builder) {
        this.builder = builder;
        ephemeral = replyConfig.ephemeral();
        editReply = replyConfig.editReply();
        keepComponents = replyConfig.keepComponents();
        keepSelections = replyConfig.keepSelections();
    }

    public void ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public void editReply(boolean editReply) {
        this.editReply = editReply;
    }

    public void keepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
    }

    public void keepSelections(boolean keepSelections) {
        this.keepSelections = keepSelections;
    }

    public ReplyConfig replyConfig() {
        return new ReplyConfig(ephemeral, editReply, keepComponents, keepSelections);
    }

    public MessageComponentTree componentTree() {
        return builder.getComponentTree();
    }

    public final Message reply() {
        defer();

        if (getJdaEvent() instanceof ComponentInteraction interaction) {
            builder.addComponents(retrieveComponents(interaction.getMessage()));
        }

        log.debug(
                "Replying to interaction \"{}\" with content: {} [ephemeral={}, editReply={}, keepComponents={}, keepSelections={}]",
                getInvocationContext().definition().displayName(), builder.build().toData(), ephemeral, editReply, keepComponents, keepSelections
        );

        var hook = ((IDeferrableCallback) getJdaEvent()).getHook();
        if (editReply) {
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build())).complete();
        }
        return hook.setEphemeral(ephemeral).sendMessage(builder.build()).complete();
    }

    private List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        MessageComponentTree componentTree = original.getComponentTree();

        if (!keepComponents) {
            return List.of();
        }

        componentTree = componentTree.replace(ComponentReplacer.of(
                ActionComponent.class,
                _ -> keepSelections,
                this::retrieveSelections
        ));

        return componentTree.getComponents();
    }

    private ActionComponent retrieveSelections(ActionComponent component) {
        return switch (component) {
            case StringSelectMenu selectMenu
                    when getJdaEvent() instanceof StringSelectInteractionEvent selectEvent -> selectMenu.createCopy()
                    .setDefaultValues(selectEvent.getValues())
                    .build();

            case EntitySelectMenu selectMenu when getJdaEvent() instanceof EntitySelectInteractionEvent selectEvent -> {

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
            default -> component;
        };
    }

    private void defer() {
        GenericInteractionCreateEvent jdaEvent = getJdaEvent();
        switch (jdaEvent) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply ->
                    deferEdit(modalEvent);
            case IMessageEditCallback callback when editReply -> deferEdit(callback);
            case IReplyCallback callback -> deferReply(callback);
            default ->
                    throw new InternalException("reply-failed", entry("getJdaEvent()", jdaEvent.getClass().getName()));
        }
        if (jdaEvent instanceof ModalInteractionEvent modalEvent) {
            editReply = modalEvent.getMessage() != null;
        }
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
