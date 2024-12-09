package com.github.kaktushose.jda.commands.dispatching.refactor;

import com.github.kaktushose.jda.commands.dispatching.refactor.event.Event;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JDAEventListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(JDAEventListener.class);
    private final Map<UUID, Runtime> runtimes;
    private final HandlerContext context;

    public JDAEventListener(HandlerContext context) {
        this.context = context;
        runtimes = new HashMap<>();
    }

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent jdaEvent) {
        Event event = mapJdaEvent(jdaEvent);

        Runtime runtime = switch (event) {
            case CommandEvent _, AutoCompleteEvent _ ->
                    runtimes.compute(UUID.randomUUID(), (id, _) -> Runtime.startNew(id, context));
//            case GenericComponentInteractionCreateEvent _, ModalInteractionEvent _ -> {
//                // TODO implement component handling
//            }
        };
        runtime.queueEvent(event);
    }

    private Event mapJdaEvent(GenericInteractionCreateEvent jdaEvent) {
        return switch (jdaEvent) {
            case SlashCommandInteractionEvent event -> new CommandEvent.SlashCommandEvent(event);
            case GenericContextInteractionEvent<?> event -> new CommandEvent.ContextCommandEvent<>(event);

            case CommandAutoCompleteInteractionEvent event -> new AutoCompleteEvent(event);
            default -> throw new UnsupportedOperationException("Unsupported jda event: %s".formatted(jdaEvent));
        };
    }
}
