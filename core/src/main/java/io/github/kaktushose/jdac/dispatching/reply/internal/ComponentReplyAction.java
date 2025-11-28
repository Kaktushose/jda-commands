package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public final class ComponentReplyAction extends ReplyAction {

    public ComponentReplyAction(ReplyConfig replyConfig, MessageTopLevelComponent component, MessageTopLevelComponent... components) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.useComponentsV2().addComponents(component).addComponents(components);
        super(replyConfig, builder);
    }

    @Override
    protected List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        return original.getComponents();
    }
}
