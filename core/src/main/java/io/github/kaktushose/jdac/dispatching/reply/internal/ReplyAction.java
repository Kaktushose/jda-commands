package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.introspection.internal.IntrospectionAccess.*;
import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public final class ReplyAction {

    private static final Logger log = JDACLogger.getLogger(ReplyAction.class);
    private final ComponentResolver<MessageTopLevelComponentUnion> componentResolver;
    private final Set<IMentionable> mentions;
    private MessageCreateBuilder builder;
    private boolean ephemeral;
    private boolean editReply;
    private boolean keepComponents;
    private boolean keepSelections;
    private boolean silent;
    private EnumSet<MentionType> allowedMentions;
    private @Nullable Replacer replacer;

    public ReplyAction(ReplyConfig replyConfig) {
        log.debug("Reply Debug: [Runtime={}]", scopedRuntime().id());
        componentResolver = new ComponentResolver<>(scopedMessageResolver(), MessageTopLevelComponentUnion.class);
        builder = new MessageCreateBuilder();
        ephemeral = replyConfig.ephemeral();
        editReply = replyConfig.editReply();
        keepComponents = replyConfig.keepComponents();
        keepSelections = replyConfig.keepSelections();
        silent = replyConfig.silent();
        allowedMentions = replyConfig.allowedMentions();
        mentions = new HashSet<>();
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

    public void silent(boolean silent) {
        this.silent = silent;
    }

    public void allowedMentions(@Nullable Collection<MentionType> allowedMentions) {
        if (allowedMentions == null) {
            this.allowedMentions = EnumSet.allOf(MentionType.class);
        } else {
            this.allowedMentions = allowedMentions.isEmpty() ? EnumSet.noneOf(MentionType.class) : EnumSet.copyOf(allowedMentions);
        }
    }

    public void mention(Collection<IMentionable> mentions) {
        this.mentions.addAll(mentions);
    }

    public ReplyConfig replyConfig() {
        return new ReplyConfig(ephemeral, editReply, keepComponents, keepSelections, silent, allowedMentions);
    }

    public MessageComponentTree componentTree() {
        return builder.getComponentTree();
    }

    public Message reply(String message, Entry... placeholder) {
        builder.setContent(scopedMessageResolver().resolve(message, scopedUserLocale(), placeholder));
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

    public Message reply(MessageTopLevelComponent component, Entry... placeholder) {
        return reply(List.of((MessageTopLevelComponentUnion) component), placeholder);
    }

    public Message reply(Collection<MessageTopLevelComponentUnion> components, Entry... placeholder) {
        components = componentResolver.resolve(components, scopedUserLocale(), Entry.toMap(placeholder));
        builder.closeFiles().clear().useComponentsV2().addComponents(components);
        return reply();
    }

    public Message reply(ComponentReplacer userProvided, ComponentReplacer resolver, Entry... placeholder) {
        replacer = new Replacer(userProvided, resolver, Entry.toMap(placeholder));
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

        if (scopedJdaEvent() instanceof ComponentInteraction interaction && keepComponents) {
            builder.addComponents(retrieveComponents(interaction.getMessage()));
            builder.useComponentsV2(interaction.getMessage().isUsingComponentsV2());
        }

        log.debug(
                "Replying to interaction \"{}\" with content: {} [ephemeral={}, editReply={}, keepComponents={}, keepSelections={}]",
                scopedInvocationContext().definition().displayName(), builder.build().toData(), ephemeral, editReply, keepComponents, keepSelections
        );

        var hook = ((IDeferrableCallback) scopedJdaEvent()).getHook();
        if (editReply) {
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build()))
                    .setAllowedMentions(allowedMentions)
                    .mention(mentions)
                    .complete();
        }
        return hook.setEphemeral(ephemeral)
                .sendMessage(builder.build())
                .setSuppressedNotifications(silent)
                .setAllowedMentions(allowedMentions)
                .mention(mentions)
                .complete();
    }

    private List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        MessageComponentTree componentTree = original.getComponentTree();

        componentTree = componentTree.replace(ComponentReplacer.of(
                ActionComponent.class,
                _ -> keepSelections,
                this::retrieveSelections
        ));

        if (replacer != null) {
            componentTree = componentTree.replace(replacer.userProvided()).replace(replacer.resolver());
            componentTree = MessageComponentTree.of(
                    componentResolver.resolve(componentTree.getComponents(), scopedUserLocale(), replacer.placeholders())
            );
        }

        return componentTree.getComponents();
    }

    private ActionComponent retrieveSelections(ActionComponent component) {
        int uniqueId = component.getUniqueId();
        component = switch (component) {
            case StringSelectMenu selectMenu
                    when scopedJdaEvent() instanceof StringSelectInteractionEvent selectEvent
                         && selectEvent.getInteraction().getUniqueId() == selectMenu.getUniqueId() ->
                    selectMenu.createCopy().setDefaultValues(selectEvent.getValues()).build();

            case EntitySelectMenu selectMenu
                    when scopedJdaEvent() instanceof EntitySelectInteractionEvent selectEvent
                         && selectEvent.getInteraction().getUniqueId() == selectMenu.getUniqueId() -> {

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
        if (uniqueId > 0) {
            component = component.withUniqueId(uniqueId);
        }
        return component;
    }

    private void defer() {
        GenericInteractionCreateEvent jdaEvent = scopedJdaEvent();
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
        if (!scopedJdaEvent().isAcknowledged()) {
            callback.deferReply(ephemeral).complete();
        }
    }

    private void deferEdit(IMessageEditCallback callback) {
        if (!scopedJdaEvent().isAcknowledged()) {
            callback.deferEdit().complete();
        }
    }

    private record Replacer(ComponentReplacer userProvided, ComponentReplacer resolver, Map<String, Object> placeholders) {}

}
