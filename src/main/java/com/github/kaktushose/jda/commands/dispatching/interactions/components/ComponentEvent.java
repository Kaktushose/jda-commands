package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ModalReplyable;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.interactions.components.GenericComponentDefinition;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent GenericComponentInteractionCreateEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see GenericEvent
 * @since 4.0.0
 */
public class ComponentEvent extends GenericEvent<GenericComponentDefinition> implements ModalReplyable {

    private final ReplyContext replyContext;

    /**
     * Constructs a ComponentEvent.
     *
     * @param context the underlying {@link Context}
     */
    public ComponentEvent(@NotNull Context context) {
        super(context);
        replyContext = new ReplyContext(context);
    }

    public ReplyContext getReplyContext() {
        return replyContext;
    }

    @Override
    public void reply() {
        Optional<MessageCreateData> optional = context.getRuntime().getLatestReply();
        if (optional.isPresent()) {
            MessageCreateData cached = optional.get();
            if (replyContext.isKeepComponents() && replyContext.getBuilder().getComponents().isEmpty()) {
                replyContext.getBuilder().setComponents(cached.getComponents());
            }

        }
        replyContext.queue();
        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }
}
