package com.github.kaktushose.jda.commands.dispatching.interactions.commands;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ModalReplyable;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent GenericCommandInteractionEvent}.
 *
 * @see GenericEvent
 * @since 4.0.0
 */
public class CommandEvent extends GenericEvent implements ModalReplyable {

    private final ReplyContext replyContext;

    /**
     * Constructs a new CommandEvent.
     *
     * @param context the underlying {@link Context}
     */
    public CommandEvent(@NotNull Context context, InteractionRegistry interactionRegistry) {
        super(context, interactionRegistry);
        replyContext = new ReplyContext(context);
    }

    @Override
    public @NotNull ReplyContext getReplyContext() {
        return replyContext;
    }

    @Override
    public void reply() {
        replyContext.queue();
        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }
}
