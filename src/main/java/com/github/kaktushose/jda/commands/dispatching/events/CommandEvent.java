package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ModalReplyable;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent GenericCommandInteractionEvent}.
 *
 * @see GenericEvent
 * @since 4.0.0
 */
public final class CommandEvent<T extends GenericCommandInteractionEvent> extends GenericEvent<T>
        implements ModalReplyable {

    private final ReplyContext replyContext;

    /**
     * Constructs a new CommandEvent.
     *
     * @param context the underlying {@link Context}
     */
    public CommandEvent(@NotNull ExecutionContext<T> context, InteractionRegistry interactionRegistry) {
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
        // TODO reimplement:
        // context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }
}
