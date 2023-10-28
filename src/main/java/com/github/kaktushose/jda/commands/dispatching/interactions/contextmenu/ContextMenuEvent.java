package com.github.kaktushose.jda.commands.dispatching.interactions.contextmenu;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ContextMenuDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

public class ContextMenuEvent extends GenericEvent implements Replyable {

    private final ContextMenuDefinition command;
    private final ContextMenuContext context;
    private final ReplyContext replyContext;

    protected ContextMenuEvent(@NotNull ContextMenuDefinition command, @NotNull ContextMenuContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.command = command;
        this.context = context;
        replyContext = new ReplyContext(context);
    }

    @Override
    public GenericContext<? extends GenericInteractionCreateEvent> getContext() {
        return context;
    }

    @Override
    public void reply() {
        replyContext.queue();
        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }

    @Override
    public @NotNull ReplyContext getReplyContext() {
        return replyContext;
    }
}
