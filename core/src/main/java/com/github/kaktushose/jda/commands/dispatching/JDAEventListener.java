package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// Handles incoming [GenericInteractionCreateEvent]s and maps them to their corresponding [Runtime], creating new ones if needed.
public final class JDAEventListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(JDAEventListener.class);
    private final Map<String, Runtime> runtimes = new ConcurrentHashMap<>();
    private final DispatchingContext context;

    public JDAEventListener(DispatchingContext context) {
        this.context = context;
    }


    @Override
    @SubscribeEvent
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent jdaEvent) {
        checkRuntimesAlive();

        Runtime runtime = switch (jdaEvent) {
            // always create new one (starter)
            case SlashCommandInteractionEvent _, GenericContextInteractionEvent<?> _,
                 CommandAutoCompleteInteractionEvent _ ->
                    runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, context, jdaEvent.getJDA()));

            // always fetch runtime (bound to runtime)
            case GenericComponentInteractionCreateEvent event when CustomId.fromEvent(event).isBound() ->
                    runtimes.get(CustomId.fromEvent(event).runtimeId());
            case ModalInteractionEvent event when CustomId.fromEvent(event).isBound() ->
                    runtimes.get(CustomId.fromEvent(event).runtimeId());

            // independent components always get their own runtime
            case GenericComponentInteractionCreateEvent event when CustomId.fromEvent(event).isIndependent() ->
                    runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, context, jdaEvent.getJDA()));
            default -> null;
        };

        if (runtime == null) {
            if (jdaEvent instanceof GenericComponentInteractionCreateEvent componentEvent && !CustomId.isInvalid(componentEvent.getComponentId())) {
                componentEvent.deferEdit().setComponents().queue();
                componentEvent.getHook()
                        .setEphemeral(true)
                        .sendMessage(context.errorMessageFactory().getTimedOutComponentMessage(jdaEvent))
                        .queue();
            } else {
                log.debug("Received unknown event: {}", jdaEvent);
            }
            return;
        }

        log.debug("Found runtime with id {} for event {}", runtime.id(), jdaEvent);

        runtime.queueEvent(jdaEvent);
    }

    private void checkRuntimesAlive() {
        runtimes.values().removeIf(Runtime::isClosed);
    }


}
