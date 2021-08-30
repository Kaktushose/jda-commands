package com.github.kaktushose.jda.commands.rewrite.dispatching;

import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.EventListener;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl.MessageParser;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.CommandRouter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.Router;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class CommandDispatcher {

    private final Object jda;
    private final boolean isShardManager;
    private final EventListener eventListener;
    private final Router router;
    private final FilterRegistry filterRegistry;

    public CommandDispatcher(Object jda, boolean isShardManager) {
        this.jda = jda;
        this.isShardManager = isShardManager;

        eventListener = new EventListener(this);
        if (isShardManager) {
            ((ShardManager) jda).addEventListener(eventListener);
        } else {
            ((JDA) jda).addEventListener(eventListener);
        }
        eventListener.addBinding(MessageReceivedEvent.class, new MessageParser());

        router = new CommandRouter();
        filterRegistry = new FilterRegistry();
    }

    public void onEvent(CommandContext context) {
        router.apply(context);

        if (context.isCancelled()) {
            return;
        }

        for (Filter filter : filterRegistry.getAll()) {
            filter.apply(context);
            if (context.isCancelled()) {
                return;
            }
        }

        // TODO parameter adapting

        // TODO command execution
    }

    public Object getJda() {
        return jda;
    }

    public boolean isShardManager() {
        return isShardManager;
    }

    public EventListener getEventListener() {
        return eventListener;
    }
}
