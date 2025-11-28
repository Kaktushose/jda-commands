package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getRuntime;

@ApiStatus.Internal
public final class ComponentReplyAction extends ReplyAction {

    private static final Logger log = LoggerFactory.getLogger(ComponentReplyAction.class);

    public ComponentReplyAction(ReplyConfig replyConfig, MessageTopLevelComponent component, MessageTopLevelComponent... components) {
        log.debug("Reply Debug: [Runtime={}]", getRuntime().id());
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.useComponentsV2().addComponents(component).addComponents(components);
        super(replyConfig, builder);
    }

    @Override
    protected List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        return original.getComponents();
    }
}
