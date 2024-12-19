package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/// Handles incoming [GenericInteractionCreateEvent]s and maps them to their corresponding [Runtime], creating new ones if needed.
public final class JDAEventListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(JDAEventListener.class);
    private final Map<String, Runtime> runtimes = new HashMap<>();
    private final HandlerContext context;

    public JDAEventListener(HandlerContext context) {
        this.context = context;
    }

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent jdaEvent) {
        Runtime runtime = switch (jdaEvent) {
            case SlashCommandInteractionEvent _, GenericContextInteractionEvent<?> _,
                 CommandAutoCompleteInteractionEvent _ ->
                    runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, context));
            case GenericComponentInteractionCreateEvent event when CustomId.isScoped(event.getComponentId()) ->
                    runtimes.get(CustomId.runtimeId(event.getComponentId()));
            case ModalInteractionEvent event when CustomId.isScoped(event.getModalId()) ->
                    runtimes.get(CustomId.runtimeId(event.getModalId()));
            case GenericComponentInteractionCreateEvent event when CustomId.isStatic(event.getComponentId()) ->
                    runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, context));
            default -> null;
        };

        if (runtime == null) {
            if (jdaEvent instanceof GenericComponentInteractionCreateEvent componentEvent && !CustomId.isInvalid(componentEvent.getComponentId())) {
                componentEvent.deferEdit().setComponents().queue();
                componentEvent.getHook()
                        .setEphemeral(true)
                        .sendMessage(context.implementationRegistry().getErrorMessageFactory().getUnknownInteractionMessage())
                        .queue();
            } else {
               log.debug("Received unknown event: {}", jdaEvent);
            }
            return;
        }

        runtime.queueEvent(jdaEvent);
    }
}
