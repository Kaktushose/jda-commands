package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.handling.*;
import com.github.kaktushose.jda.commands.dispatching.handling.command.ContextCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.command.SlashCommandHandler;
import com.github.kaktushose.jda.commands.dispatching.instantiation.InstantiationContext;
import com.github.kaktushose.jda.commands.dispatching.instantiation.Instantiator;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/// A [Runtime] delegates the jda events to their corresponding [EventHandler] and manages the used virtual threads.
///
/// A new [Runtime] is created each time an [SlashCommandInteractionEvent], [GenericContextInteractionEvent] or [CommandAutoCompleteInteractionEvent] is provided by jda
/// or if an interaction is marked as 'independent'.
/// Runtimes are executed in parallel, but events are processed sequentially by each runtime.
/// Every [EventHandler] called by this [Runtime] is executed in its own virtual thread, isolated from the runtime one.
///
/// @implNote Each [Runtime] is based on a [BlockingQueue] in which jda events, belonging to this
/// runtime, are put by the [JDAEventListener] running on the jda event thread.
/// Each runtime than has its own virtual thread that takes events from this queue and executes them sequentially but
/// each in its own (sub) virtual thread. Therefore, the virtual thread in which the user code will be called, only exists for
/// the lifespan of one "interaction" and cannot interfere with other interactions on the same or other runtimes.
@ApiStatus.Internal
public final class Runtime implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(Runtime.class);
    private final ExpirationStrategy expirationStrategy;
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
    private final Instantiator instantiator;
    private LocalDateTime lastActivity = LocalDateTime.now();

    private Runtime(@NotNull String id, @NotNull DispatchingContext dispatchingContext) {
        this.id = id;
        this.instances = new HashMap<>();
        expirationStrategy = dispatchingContext.expirationStrategy();
        blockingQueue = new LinkedBlockingQueue<>();
        slashCommandHandler = new SlashCommandHandler(dispatchingContext);
        autoCompleteHandler = new AutoCompleteHandler(dispatchingContext);
        contextCommandHandler = new ContextCommandHandler(dispatchingContext);
        componentHandler = new ComponentHandler(dispatchingContext);
        modalHandler = new ModalHandler(dispatchingContext);
        this.instantiator = dispatchingContext.instantiator();
        this.executionThread = Thread.ofVirtual()
                .name("JDAC Runtime-Thread %s".formatted(id))
                .uncaughtExceptionHandler((_, e) -> log.error("Error in JDA-Commands Runtime:", e))
                .unstarted(this::checkForEvents);
    }

    @NotNull
    public static Runtime startNew(String id, DispatchingContext dispatchingContext) {
        var runtime = new Runtime(id, dispatchingContext);
        runtime.executionThread.start();

        log.debug("Created new runtime with id {}", id);

        return runtime;
    }

    private void checkForEvents() {
        try {
            while (!Thread.interrupted()) {
                GenericInteractionCreateEvent incomingEvent = blockingQueue.take();

                Thread.ofVirtual().name("JDAC EventHandler-Thread %s".formatted(id)).start(() -> executeHandler(incomingEvent)).join();
            }
        } catch (InterruptedException _) {
        }

        log.debug("Runtime finished");
    }

    private void executeHandler(GenericInteractionCreateEvent incomingEvent) {
        lastActivity = LocalDateTime.now();
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

    @NotNull
    public String id() {
        return id;
    }

    public void queueEvent(GenericInteractionCreateEvent event) {
        blockingQueue.add(event);
    }

    @NotNull
    public KeyValueStore keyValueStore() {
        return keyValueStore;
    }

    @SuppressWarnings("unchecked")
    public <T> T interactionClass(Class<T> clazz) {
        return (T) instances.computeIfAbsent(clazz, _ -> instantiator.instantiate(clazz, instantiationContext()));
    }

    private InstantiationContext instantiationContext() {
        return new InstantiationContext(this);
    }

    public Object instance(InteractionDefinition definition) {
        return interactionClass(definition.classDescription().clazz());
    }

    @Override
    public void close() {
        executionThread.interrupt();
    }

    public boolean isClosed() {
        if (expirationStrategy instanceof ExpirationStrategy.Inactivity(long minutes) &&
                lastActivity.isBefore(LocalDateTime.now().minusMinutes(minutes))) {
            close();
            return true;
        }

        return !executionThread.isAlive();
    }
}
