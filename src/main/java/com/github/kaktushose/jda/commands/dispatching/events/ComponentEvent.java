package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ModalReplyable;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent GenericComponentInteractionCreateEvent}.
 *
 * @see GenericEvent
 * @since 4.0.0
 */
public final class ComponentEvent extends GenericEvent<GenericComponentInteractionCreateEvent> implements ModalReplyable {

    private final ReplyContext replyContext;

    /**
     * Constructs a ComponentEvent.
     *
     * @param context the underlying {@link Context}
     */
    public ComponentEvent(@NotNull ExecutionContext<GenericComponentInteractionCreateEvent> context, InteractionRegistry interactionRegistry) {
        super(context, interactionRegistry);
        replyContext = new ReplyContext(context);
    }

    @NotNull
    public ReplyContext getReplyContext() {
        return replyContext;
    }

    @Override
    public void reply() {
        // todo reimplement
        throw new UnsupportedOperationException("currently now supported");
//        Optional<MessageCreateData> optional = context.getRuntime().getLatestReply();
//        if (optional.isPresent()) {
//            MessageCreateData cached = optional.get();
//            if (replyContext.isKeepComponents() && replyContext.getBuilder().getComponents().isEmpty()) {
//                replyContext.getBuilder().setComponents(cached.getComponents());
//            }
//
//        }
//        replyContext.queue();
//        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }
}
