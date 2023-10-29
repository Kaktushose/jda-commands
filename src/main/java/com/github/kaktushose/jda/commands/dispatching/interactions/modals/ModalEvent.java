package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModalEvent extends GenericEvent implements Replyable {

    private final ModalContext context;
    private final ReplyContext replyContext;

    protected ModalEvent(ModalDefinition modal, ModalContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.context = context;
        replyContext = new ReplyContext(context);
    }

    @Override
    public GenericContext<? extends GenericInteractionCreateEvent> getContext() {
        return context;
    }

    @Override
    public @NotNull ReplyContext getReplyContext() {
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
        replyContext.setEditReply(false).queue();
        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }
}
