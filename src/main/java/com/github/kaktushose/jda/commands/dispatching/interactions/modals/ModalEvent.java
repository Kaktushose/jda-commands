package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.ModalInteractionEvent ModalInteractionEvent}.
 *
 * @see GenericEvent
 * @since 4.0.0
 */
public class ModalEvent extends GenericEvent implements Replyable {

    private final ReplyContext replyContext;

    protected ModalEvent(Context context, InteractionRegistry interactionRegistry) {
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
    }
}
