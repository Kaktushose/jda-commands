package com.github.kaktushose.jda.commands.dispatching.refactor;

import com.github.kaktushose.jda.commands.dispatching.refactor.event.Event;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.AutoCompleteHandler;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.CommandHandler;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ApiStatus.Internal
public final class Runtime implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(Runtime.class);
    private final CommandHandler commandHandler;
    private final AutoCompleteHandler autoCompleteHandler;
    private final UUID id;
    private final Map<Class<?>, Object> instances;
    private final BlockingQueue<Event> blockingQueue;
    private final Thread executionThread;
    private MessageCreateData latestReply;


    private Runtime(UUID id, HandlerContext handlerContext) {
        this.id = id;
        this.instances = new HashMap<>();
        blockingQueue = new LinkedBlockingQueue<>();
        commandHandler = new CommandHandler(handlerContext);
        autoCompleteHandler = new AutoCompleteHandler(handlerContext);

        this.executionThread = Thread.ofVirtual()
                .name("JDA-Commands Runtime-Thread")
                .uncaughtExceptionHandler((t, e) -> log.error("Error in JDA-Commands Runtime:", new InvocationTargetException(e)))
                .unstarted(() -> {
                    try {
                        while (!Thread.interrupted()) {
                            switch (blockingQueue.take()) {
                                case CommandEvent event -> commandHandler.accept(event, this);
                                case AutoCompleteEvent event -> autoCompleteHandler.accept(event, this);
                            }
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
    }

    public static Runtime startNew(UUID id, HandlerContext handlerContext) {
        var runtime = new Runtime(id, handlerContext);
        runtime.executionThread.start();
        return runtime;
    }

    public UUID id() {
        return id;
    }

    public void queueEvent(Event event) {
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

    @Override
    public void close()  {
        executionThread.interrupt();
    }
}
