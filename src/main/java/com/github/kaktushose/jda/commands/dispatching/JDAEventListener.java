package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
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
            case ModalInteractionEvent event when CustomId.isStatic(event.getModalId()) ->
                    runtimes.compute(UUID.randomUUID().toString(), (id, _) -> Runtime.startNew(id, context));
            default -> throw new UnsupportedOperationException("Unsupported jda event: %s".formatted(jdaEvent));
        };

        if (runtime == null) {
            new MessageReply(jdaEvent, new ReplyConfig()).reply(
                    context.implementationRegistry()
                            .getErrorMessageFactory()
                            .getUnknownInteractionMessage()
            );
            return;
        }

        runtime.queueEvent(jdaEvent);
    }
}
