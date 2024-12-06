package com.github.kaktushose.jda.commands.dispatching.refactor;

import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandDispatcher;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ApiStatus.Internal
public final class Runtime {

    private static final Logger log = LoggerFactory.getLogger(Runtime.class);
    private final CommandDispatcher commandDispatcher;
    private final UUID id;
    private final Map<Class<?>, Object> instances;
    private final BlockingQueue<GenericInteractionCreateEvent> blockingQueue;
    private MessageCreateData latestReply;


    public Runtime(UUID id, DispatcherContext dispatcherContext) {
        this.id = id;
        this.instances = new HashMap<>();
        blockingQueue = new LinkedBlockingQueue<>();
        commandDispatcher = new CommandDispatcher(dispatcherContext);
        Thread.ofVirtual()
                .name("JDA-Commands Runtime-Thread")
                .uncaughtExceptionHandler((t, e) -> log.error("Error in JDA-Commands Runtime:", new InvocationTargetException(e)))
                .start(() -> {
                    try {
                        while (true) {
                            var genericEvent = blockingQueue.take();
                            switch (genericEvent) {
                                case GenericCommandInteractionEvent event -> commandDispatcher.onEvent(event, this);
                                default -> throw new IllegalStateException("Unexpected value: " + genericEvent);
                            }
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
    }

    public UUID id() {
        return id;
    }

    public void queueEvent(GenericInteractionCreateEvent event) {
        System.out.println(event);
        blockingQueue.add(event);
    }

    public MessageCreateData latestReply() {
        return latestReply;
    }

    public void latestReply(MessageCreateData latestReply) {
        this.latestReply = latestReply;
    }

    public Object instance(GenericInteractionDefinition definition) {
        return instances.computeIfAbsent(definition.getMethod().getClass(), _ -> {
            try {
                return definition.newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
