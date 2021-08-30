package com.github.kaktushose.jda.commands.rewrite.dispatching;

import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.EventListener;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl.MessageParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class CommandDispatcher {

    private final Object jda;
    private final boolean isShardManager;
    private final EventListener eventListener;

    public CommandDispatcher(Object jda, boolean isShardManager) {
        this.jda = jda;
        this.isShardManager = isShardManager;

        eventListener = new EventListener();
        if (isShardManager) {
            ((ShardManager) jda).addEventListener(eventListener);
        } else {
            ((JDA) jda).addEventListener(eventListener);
        }
        eventListener.addBinding(MessageReceivedEvent.class, new MessageParser());
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
