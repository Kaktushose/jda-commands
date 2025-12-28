package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
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
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.*;
import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getUserLocale;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public final class ReplyAction {

    private static final Logger log = LoggerFactory.getLogger(ReplyAction.class);
    private final ComponentResolver<MessageTopLevelComponentUnion> componentResolver;
    private MessageCreateBuilder builder;
    private boolean ephemeral;
    private boolean editReply;
    private boolean keepComponents;
    private boolean keepSelections;

    public ReplyAction(ReplyConfig replyConfig) {
        log.debug("Reply Debug: [Runtime={}]", getRuntime().id());
        componentResolver = new ComponentResolver<>(getFramework().messageResolver(), MessageTopLevelComponentUnion.class);
        builder = new MessageCreateBuilder();
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

    public Message reply(String message, Entry... placeholder) {
        builder.setContent(getFramework().messageResolver().resolve(message, getUserLocale(), Entry.toMap(placeholder)));
        return reply();
    }

    public Message reply(MessageEmbed first, MessageEmbed... additional) {
        builder.setEmbeds(Stream.concat(Stream.of(first), Arrays.stream(additional)).toList());
        return reply();
    }

    public Message reply(MessageCreateData data) {
        builder = MessageCreateBuilder.from(data);
        return reply();
    }

    public Message reply(List<MessageTopLevelComponentUnion> components, Entry... placeholder) {
        components = componentResolver.resolve(components, getUserLocale(), Entry.toMap(placeholder));
        builder.closeFiles().clear().useComponentsV2().addComponents(components);
        return reply();
    }

    public Message reply(ComponentReplacer replacer, Entry... placeholder) {
        var components = builder.getComponentTree().replace(replacer).getComponents();
        components = componentResolver.resolve(components, getUserLocale(), Entry.toMap(placeholder));
        builder.setComponents(components);
        return reply();
    }

    public void builder(Consumer<MessageCreateBuilder> builder) {
        builder.accept(this.builder);
    }

    public void addComponents(MessageTopLevelComponent... components) {
        builder.addComponents(components);
    }

    public void addEmbeds(MessageEmbed... embeds) {
        builder.addEmbeds(embeds);
    }

    public Message reply() {
        defer();

        if (getJdaEvent() instanceof ComponentInteraction interaction && keepComponents) {
            builder.addComponents(retrieveComponents(interaction.getMessage()));
            builder.useComponentsV2(interaction.getMessage().isUsingComponentsV2());
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
            callback.deferReply(ephemeral).complete();
        }
    }

    private void deferEdit(IMessageEditCallback callback) {
        if (!getJdaEvent().isAcknowledged()) {
            callback.deferEdit().complete();
        }
    }
}
