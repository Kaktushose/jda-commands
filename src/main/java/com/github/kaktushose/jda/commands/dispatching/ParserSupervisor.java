package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonParser;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandParser;
import com.github.kaktushose.jda.commands.dispatching.commands.autocomplete.AutoCompleteParser;
import com.github.kaktushose.jda.commands.dispatching.menus.SelectMenuParser;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for {@link GenericParser Parsers}. This is also the event listener that will call the corresponding parser.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see GenericParser
 * @since 2.0.0
 */
public class ParserSupervisor extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ParserSupervisor.class);
    private final DispatcherSupervisor dispatcher;
    private final Map<Class<? extends GenericEvent>, GenericParser<? extends GenericEvent>> listeners;

    /**
     * Constructs a new ParserSupervisor.
     *
     * @param dispatcher the calling {@link DispatcherSupervisor}
     */
    public ParserSupervisor(@NotNull DispatcherSupervisor dispatcher) {
        listeners = new HashMap<>();
        this.dispatcher = dispatcher;
        register(SlashCommandInteractionEvent.class, new CommandParser());
        register(ButtonInteractionEvent.class, new ButtonParser());
        register(EntitySelectInteractionEvent.class, new SelectMenuParser());
        register(StringSelectInteractionEvent.class, new SelectMenuParser());
        register(CommandAutoCompleteInteractionEvent.class, new AutoCompleteParser());
    }

    /**
     * Registers a new {@link GenericParser} for the given subtype of {@link GenericEvent}.
     *
     * @param listener the subtype of {@link GenericEvent}
     * @param parser   the {@link GenericParser} to register
     */
    public void register(@NotNull Class<? extends GenericEvent> listener, @NotNull GenericParser<? extends GenericEvent> parser) {
        listeners.put(listener, parser);
        log.debug("Registered parser {} for event {}", parser.getClass().getName(), listener.getSimpleName());
    }

    /**
     * Unregisters the {@link GenericParser} for the given subtype of {@link GenericEvent}.
     *
     * @param listener the subtype of {@link GenericEvent}
     */
    public void unregister(@NotNull Class<? extends GenericEvent> listener) {
        listeners.remove(listener);
        log.debug("Unregistered parser binding for event {}", listener.getSimpleName());
    }

    /**
     * Distributes {@link GenericEvent GenericEvents} to the corresponding parser. If the parsing didn't fail, will call
     * {@link DispatcherSupervisor#onGenericEvent(GenericContext)}
     *
     * @param event the {@link GenericEvent GenericEvents} to distribute
     */
    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (!listeners.containsKey(event.getClass())) {
            return;
        }
        log.debug("Received {}", event.getClass().getSimpleName());
        GenericParser<?> parser = listeners.get(event.getClass());
        log.debug("Calling {}", parser.getClass().getName());

        GenericContext<? extends GenericInteractionCreateEvent> context = parser.parseInternal(event, dispatcher.getJdaCommands());

        if (context.isCancelled()) {
            if (context.getErrorMessage() != null) {
                // TODO send message
            }
            return;
        }
        dispatcher.onGenericEvent(context);
    }
}
