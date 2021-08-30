package com.github.kaktushose.jda.commands.rewrite.dispatching.parser;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandDispatcher;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EventListener extends ListenerAdapter {

    private final CommandDispatcher dispatcher;
    private final Map<Class<? extends GenericEvent>, Parser<? extends GenericEvent>> listeners;

    public EventListener(CommandDispatcher dispatcher) {
        listeners = new HashMap<>();
        this.dispatcher = dispatcher;
    }

    public void addBinding(Class<? extends GenericEvent> listener, Parser<? extends GenericEvent> parser) {
        listeners.put(listener, parser);
    }

    public void removeBinding(Class<? extends GenericEvent> listener) {
        listeners.remove(listener);
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (listeners.containsKey(event.getClass())) {
            Parser<?> parser = listeners.get(event.getClass());
            CommandContext context = parser.parseInternal(event);
            dispatcher.onEvent(context);
        }
    }
}
