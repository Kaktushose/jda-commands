package io.github.kaktushose.jdac.dispatching.reply.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class ComponentReplyAction extends ReplyAction {

    public ComponentReplyAction(ReplyConfig replyConfig, MessageCreateBuilder builder) {
        super(replyConfig, builder);
    }

    @Override
    public void builder(Consumer<MessageCreateBuilder> consumer) {
        consumer.accept(builder);
        if (!builder.isUsingComponentsV2()) {
            throw new IllegalArgumentException("TODO: proper error message");
        }
    }

    @Override
    protected List<MessageTopLevelComponentUnion> retrieveComponents(Message original) {
        return original.getComponents();
    }
}
