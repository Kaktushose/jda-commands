package com.github.kaktushose.jda.commands.dispatching.refactor;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
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
    private final DispatcherContext context;

    public JDAEventListener(DispatcherContext context) {
        this.context = context;
        runtimes = new HashMap<>();
    }

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent event) {
        switch (event) {
            case GenericCommandInteractionEvent _, CommandAutoCompleteInteractionEvent _ ->
                    runtimes.compute(UUID.randomUUID(), (id, _) -> Runtime.create(id, context)).queueEvent(event);
            case GenericComponentInteractionCreateEvent _, ModalInteractionEvent _ -> {
                // TODO implement component handling
            }
            default -> {
            }
        }

    }
}
