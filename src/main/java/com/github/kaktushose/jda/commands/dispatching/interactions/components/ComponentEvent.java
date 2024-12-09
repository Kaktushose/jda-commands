package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ModalReplyable;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
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
public final class ComponentEvent extends GenericEvent<GenericComponentInteractionCreateEvent, EphemeralInteractionDefinition> implements ModalReplyable {

    private final ReplyContext replyContext;

    /**
     * Constructs a ComponentEvent.
     *
     * @param context the underlying {@link Context}
     */
    public ComponentEvent(@NotNull ExecutionContext<GenericComponentInteractionCreateEvent, EphemeralInteractionDefinition> context, InteractionRegistry interactionRegistry) {
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
