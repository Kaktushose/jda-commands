package com.github.kaktushose.jda.commands.rewrite.dispatching.parser;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EventListener extends ListenerAdapter {

    private final Map<Class<? extends GenericEvent>, Parser<? extends GenericEvent>> listeners;

    public EventListener() {
        listeners = new HashMap<>();
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
            listeners.get(event.getClass()).parseInternal(event);
        }
    }
}
