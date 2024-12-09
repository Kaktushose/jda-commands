package com.github.kaktushose.jda.commands.dispatching.interactions.commands;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ModalReplyable;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent GenericCommandInteractionEvent}.
 *
 * @see GenericEvent
 * @since 4.0.0
 */
public final class CommandEvent<T extends GenericInteractionCreateEvent, U extends GenericInteractionDefinition> extends GenericEvent<T, U> implements ModalReplyable {

    private final ReplyContext replyContext;

    /**
     * Constructs a new CommandEvent.
     *
     * @param context the underlying {@link Context}
     */
    public CommandEvent(@NotNull ExecutionContext<T, U> context, InteractionRegistry interactionRegistry) {
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
