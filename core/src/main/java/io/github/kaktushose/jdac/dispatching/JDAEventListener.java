package io.github.kaktushose.jdac.dispatching;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.ICustomIdInteraction;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// Handles incoming [GenericInteractionCreateEvent]s and maps them to their corresponding [Runtime], creating new ones if needed.
@ApiStatus.Internal
public final class JDAEventListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(JDAEventListener.class);
    private final Map<String, Runtime> runtimes = new ConcurrentHashMap<>();
    private final Resolver resolver;

    public JDAEventListener(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    @SubscribeEvent
    public void onGenericInteractionCreate(GenericInteractionCreateEvent jdaEvent) {
        checkRuntimesAlive();

        Runtime runtime = switch (jdaEvent) {
            // always create new one for command events (starter)
            case SlashCommandInteractionEvent _, GenericContextInteractionEvent<?> _,
                 CommandAutoCompleteInteractionEvent _ ->
                    runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, resolver, jdaEvent.getJDA()));
            // check events with custom id (components or modals)
            case ICustomIdInteraction interaction -> {
                if (CustomId.isInvalid(interaction.getCustomId())) {
                    yield null;
                }
                CustomId customId = CustomId.fromMerged(interaction.getCustomId());
                if (customId.isBound()) {
                    yield runtimes.get(customId.runtimeId());
                }
                yield runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, resolver, jdaEvent.getJDA()));
            }
            default -> null;
        };

        if (runtime == null) {
            if (jdaEvent instanceof GenericComponentInteractionCreateEvent componentEvent && !CustomId.isInvalid(componentEvent.getComponentId())) {
                if (componentEvent.getMessage().isUsingComponentsV2()) {
                    componentEvent.deferReply(true).queue();
                    componentEvent.getMessage().delete().queue();
                } else {
                    componentEvent.deferEdit().setComponents().queue();
                }
                componentEvent.getHook()
                        .setEphemeral(true)
                        .sendMessage(resolver.get(Property.ERROR_MESSAGE_FACTORY).getTimedOutComponentMessage(jdaEvent))
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
