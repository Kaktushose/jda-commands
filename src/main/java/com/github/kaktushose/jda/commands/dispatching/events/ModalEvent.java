package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.ModalInteractionEvent ModalInteractionEvent}.
 *
 * @see GenericEvent
 * @since 4.0.0
 */
public final class ModalEvent extends GenericEvent<ModalInteractionEvent, ModalDefinition> implements Replyable {

    private final ReplyContext replyContext;

   public ModalEvent(ExecutionContext<ModalInteractionEvent, ModalDefinition> context, InteractionRegistry interactionRegistry) {
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
