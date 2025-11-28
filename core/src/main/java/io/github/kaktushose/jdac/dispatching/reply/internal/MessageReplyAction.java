package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getFramework;
import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getJdaEvent;

/// Handling of all the business logic of sending messages.
@ApiStatus.Internal
public final class MessageReplyAction extends ReplyAction {

    /// Constructs a new MessageReplyAction.
    ///
    /// @param replyConfig the [ReplyConfig] to use
    public MessageReplyAction(ReplyConfig replyConfig) {
        super(replyConfig, new MessageCreateBuilder());
    }

    public Message reply(String message, Entry... placeholder) {
        builder.setContent(getFramework().messageResolver().resolve(message, RichInvocationContext.getUserLocale(), placeholder));
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

    @Override
    public List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        MessageComponentTree componentTree = original.getComponentTree();

        if (!replyConfig.keepComponents()) {
            return original.getComponents();
        }

        for (MessageTopLevelComponentUnion topLevel : componentTree.getComponents()) {
            for (ActionComponent oldComponent : topLevel.asActionRow().getActionComponents()) {
                ActionComponent newComponent = switch (oldComponent) {
                    case StringSelectMenu selectMenu
                            when getJdaEvent() instanceof StringSelectInteractionEvent selectEvent && replyConfig.keepSelections() ->
                            selectMenu.createCopy()
                                    .setDefaultValues(selectEvent.getValues())
                                    .build();

                    case EntitySelectMenu selectMenu when getJdaEvent() instanceof EntitySelectInteractionEvent selectEvent && replyConfig.keepSelections() -> {

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
}
