package io.github.kaktushose.jdac.dispatching;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.InternalProperties;
import io.github.kaktushose.jdac.configuration.internal.Properties;
import io.github.kaktushose.jdac.dispatching.context.KeyValueStore;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.handling.AutoCompleteHandler;
import io.github.kaktushose.jdac.dispatching.handling.ComponentHandler;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;
import io.github.kaktushose.jdac.dispatching.handling.ModalHandler;
import io.github.kaktushose.jdac.dispatching.handling.command.ContextCommandHandler;
import io.github.kaktushose.jdac.dispatching.handling.command.SlashCommandHandler;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import io.github.kaktushose.jdac.introspection.lifecycle.events.RuntimeCloseEvent;
import io.github.kaktushose.jdac.introspection.lifecycle.events.RuntimeOpenEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.Closeable;
import java.time.LocalDateTime;
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

    private static final Logger log = JDACLogger.getLogger(Runtime.class);

    private final SlashCommandHandler slashCommandHandler;
    private final AutoCompleteHandler autoCompleteHandler;
    private final ContextCommandHandler contextCommandHandler;
    private final ComponentHandler componentHandler;
    private final ModalHandler modalHandler;

    private final String id;
    private final BlockingQueue<GenericInteractionCreateEvent> eventQueue = new LinkedBlockingQueue<>();
    private final Thread executionThread;

    private final KeyValueStore keyValueStore = new KeyValueStore();

    private final IntrospectionImpl introspection;

    private LocalDateTime lastActivity = LocalDateTime.now();

    private Runtime(String id, IntrospectionImpl baseIntrospection, JDA jda) {
        this.id = id;

        this.introspection = Properties.Builder.newRestricted()
                .addFallback(Property.JDA, _ -> jda)
                .addFallback(InternalProperties.RUNTIME, _ -> this)
                .addFallback(Property.RUNTIME_ID, _ -> id)
                .addFallback(Property.KEY_VALUE_STORE, _ -> keyValueStore())
                .createIntrospection(baseIntrospection, Stage.RUNTIME);

        slashCommandHandler = new SlashCommandHandler(introspection);
        autoCompleteHandler = new AutoCompleteHandler(introspection);
        contextCommandHandler = new ContextCommandHandler(introspection);
        componentHandler = new ComponentHandler(introspection);
        modalHandler = new ModalHandler(introspection);

        this.executionThread = Thread.ofVirtual()
                .name("JDAC Runtime-Thread %s".formatted(id))
                .uncaughtExceptionHandler((_, e) -> log.error("Error in JDA-Commands Runtime:", e))
                .unstarted(this::checkForEvents);
    }

    public static Runtime startNew(String id, IntrospectionImpl introspection, JDA jda) {
        var runtime = new Runtime(id, introspection, jda);
        runtime.executionThread.start();

        log.debug("Created new runtime with id {}", id);

        return runtime;
    }

    private void checkForEvents() {
        ScopedValue.where(IntrospectionImpl.INTROSPECTION, introspection).run(() -> {
            introspection.publish(new RuntimeOpenEvent(id));

            try {
                while (!Thread.interrupted()) {
                    GenericInteractionCreateEvent incomingEvent = eventQueue.take();

                    Thread.ofVirtual().name("JDAC EventHandler-Thread %s".formatted(id)).start(() -> executeHandler(incomingEvent)).join();
                }
            } catch (InterruptedException _) {
            }

            log.debug("Runtime finished");
        });
    }

    private void executeHandler(GenericInteractionCreateEvent incomingEvent) {
        lastActivity = LocalDateTime.now();
        switch (incomingEvent) {
            case SlashCommandInteractionEvent event -> slashCommandHandler.accept(event, this);
            case GenericContextInteractionEvent<?> event -> contextCommandHandler.accept(event, this);
            case CommandAutoCompleteInteractionEvent event -> autoCompleteHandler.accept(event, this);
            case GenericComponentInteractionCreateEvent event -> componentHandler.accept(event, this);
            case ModalInteractionEvent event -> modalHandler.accept(event, this);
            default -> throw new InternalException("default-switch");
        }
    }

    public String id() {
        return id;
    }

    public void queueEvent(GenericInteractionCreateEvent event) {
        eventQueue.add(event);
    }

    public KeyValueStore keyValueStore() {
        return keyValueStore;
    }

    public <T> T interactionInstance(Class<T> clazz) {
        return introspection.get(Property.INTERACTION_CONTROLLER_INSTANTIATOR).instance(clazz, introspection);
    }

    @Override
    public void close() {
        introspection.publish(new RuntimeCloseEvent(id));
        executionThread.interrupt();
    }

    public boolean isClosed() {
        if (introspection.get(Property.EXPIRATION_STRATEGY) instanceof ExpirationStrategy.Inactivity(long minutes) &&
            lastActivity.isBefore(LocalDateTime.now().minusMinutes(minutes))) {
            close();
            return true;
        }

        return !executionThread.isAlive();
    }
}
