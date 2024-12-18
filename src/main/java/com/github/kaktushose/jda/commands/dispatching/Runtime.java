package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.context.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.handling.*;
import com.github.kaktushose.jda.commands.dispatching.handling.command.ContextCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.command.SlashCommandHandler;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ApiStatus.Internal
public final class Runtime implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(Runtime.class);
    private final SlashCommandHandler slashCommandHandler;
    private final AutoCompleteHandler autoCompleteHandler;
    private final ContextCommandHandler contextCommandHandler;
    private final ComponentHandler componentHandler;
    private final String id;
    private final Map<Class<?>, Object> instances;
    private final BlockingQueue<GenericInteractionCreateEvent> blockingQueue;
    private final Thread executionThread;
    private final KeyValueStore keyValueStore = new KeyValueStore();
    private final ModalHandler modalHandler;

    private Runtime(String id, HandlerContext handlerContext) {
        this.id = id;
        this.instances = new HashMap<>();
        blockingQueue = new LinkedBlockingQueue<>();
        slashCommandHandler = new SlashCommandHandler(handlerContext);
        autoCompleteHandler = new AutoCompleteHandler(handlerContext);
        contextCommandHandler = new ContextCommandHandler(handlerContext);
        componentHandler = new ComponentHandler(handlerContext);
        modalHandler = new ModalHandler(handlerContext);
        this.executionThread = Thread.ofVirtual()
                .name("JDA-Commands Runtime-Thread for ID %s".formatted(id))
                .uncaughtExceptionHandler((_, e) -> log.error("Error in JDA-Commands Runtime:", e))
                .unstarted(this::checkForEvents);
    }

    private void checkForEvents() {
        try {
            while (!Thread.interrupted()) {
                GenericInteractionCreateEvent incomingEvent = blockingQueue.take();

                Thread.ofVirtual().name("JDA-Commands EventHandler Thread").start(() -> executeHandler(incomingEvent)).join();
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void executeHandler(GenericInteractionCreateEvent incomingEvent) {
        switch (incomingEvent) {
            case SlashCommandInteractionEvent event -> slashCommandHandler.accept(event, this);
            case GenericContextInteractionEvent<?> event -> contextCommandHandler.accept(event, this);
            case CommandAutoCompleteInteractionEvent event -> autoCompleteHandler.accept(event, this);
            case GenericComponentInteractionCreateEvent event -> componentHandler.accept(event, this);
            case ModalInteractionEvent event -> modalHandler.accept(event, this);
            default ->
                    throw new IllegalStateException("Should not occur. Please report this error the the devs of jda-commands.");
        }
    }

    public static Runtime startNew(String id, HandlerContext handlerContext) {
        var runtime = new Runtime(id, handlerContext);
        runtime.executionThread.start();
        return runtime;
    }

    public String id() {
        return id;
    }

    public void queueEvent(GenericInteractionCreateEvent event) {
        blockingQueue.add(event);
    }

    public KeyValueStore keyValueStore() {
        return keyValueStore;
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
    public void close() {
        executionThread.interrupt();
    }
}
