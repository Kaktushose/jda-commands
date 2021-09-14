package com.github.kaktushose.jda.commands.dispatching.parser;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ParserSupervisor extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ParserSupervisor.class);
    private final CommandDispatcher dispatcher;
    private final Map<Class<? extends GenericEvent>, Parser<? extends GenericEvent>> listeners;

    public ParserSupervisor(CommandDispatcher dispatcher) {
        listeners = new HashMap<>();
        this.dispatcher = dispatcher;
    }

    public void register(Class<? extends GenericEvent> listener, Parser<? extends GenericEvent> parser) {
        listeners.put(listener, parser);
        log.debug("Registered parser {} for event {}", parser.getClass().getName(), listener.getSimpleName());
    }

    public void unregister(Class<? extends GenericEvent> listener) {
        listeners.remove(listener);
        log.debug("Unregistered parser binding for event {}", listener.getSimpleName());
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (!listeners.containsKey(event.getClass())) {
            return;
        }
        log.debug("Received {}", event.getClass().getSimpleName());
        Parser<?> parser = listeners.get(event.getClass());
        log.debug("Calling {}", parser.getClass().getName());
        CommandContext context = parser.parseInternal(event);
        dispatcher.onEvent(context);
    }
}
