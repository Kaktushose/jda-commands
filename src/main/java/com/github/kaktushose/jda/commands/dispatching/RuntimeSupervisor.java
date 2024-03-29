package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Supervisor that creates and stores {@link InteractionRuntime InteractionRuntimes}. This supervisor will create a
 * new {@link InteractionRuntime} for every command execution with a TTL of 15 minutes.
 *
 * @since 4.0.0
 */
public class RuntimeSupervisor {

    private final Map<String, InteractionRuntime> runtimes;
    private final Map<String, Object> staticInstances;
    private final ScheduledExecutorService executor;
    private final DependencyInjector injector;

    /**
     * Constructs a new RuntimeSupervisor.
     */
    public RuntimeSupervisor(DependencyInjector injector) {
        this.injector = injector;
        runtimes = new HashMap<>();
        staticInstances = new HashMap<>();
        executor = new ScheduledThreadPoolExecutor(4);
    }

    /**
     * Creates a new {@link InteractionRuntime}.
     *
     * @param event       the {@link GenericCommandInteractionEvent} to create the {@link InteractionRuntime} for
     * @param interaction the {@link GenericInteractionDefinition} to create the {@link InteractionRuntime} from
     * @return a new {@link InteractionRuntime} with a TTL of 15 minutes
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException    if this Constructor object is enforcing Java language access control and
     *                                   the underlying constructor is inaccessible
     */
    public InteractionRuntime newRuntime(GenericCommandInteractionEvent event, GenericInteractionDefinition interaction)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object instance = interaction.newInstance();

        injector.inject(instance);

        String id = event.getId();
        InteractionRuntime runtime = new InteractionRuntime(id, instance);

        runtimes.put(id, runtime);
        executor.schedule(() -> runtimes.remove(id), 15, TimeUnit.MINUTES);

        return runtime;
    }

    /**
     * Gets an instance of the given {@link GenericInteractionDefinition}. If no instance exists yet, creates, stores and then
     * returns the instance.
     *
     * @param event       the {@link CommandAutoCompleteInteractionEvent} to  get the instance for
     * @param interaction the {@link GenericInteractionDefinition} to create or get an instance from
     * @return an instance of the provided {@link GenericInteractionDefinition}
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException    if this Constructor object is enforcing Java language access control and
     *                                   the underlying constructor is inaccessible
     */
    public Object getOrCreateInstance(CommandAutoCompleteInteractionEvent event, GenericInteractionDefinition interaction)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String name = event.getFullCommandName();
        if (staticInstances.containsKey(name)) {
            return staticInstances.get(name);
        } else {
            Object instance = interaction.newInstance();
            staticInstances.put(name, instance);
            return instance;
        }
    }

    /**
     * Gets an {@link Optional} holding the {@link InteractionRuntime}. Returns an empty {@link Optional} if no
     * {@link InteractionRuntime} has been created yet by calling
     * {@link #newRuntime(GenericCommandInteractionEvent, GenericInteractionDefinition)}, if the underlying component wasn't
     * created by jda-commands or if the {@link InteractionRuntime} expired.
     *
     * @param event the {@link GenericComponentInteractionCreateEvent} to get the {@link InteractionRuntime} for
     * @return an {@link Optional} holding the {@link InteractionRuntime}
     */
    public Optional<InteractionRuntime> getRuntime(GenericComponentInteractionCreateEvent event) {
        String[] split = event.getComponentId().split("\\.");
        if (split.length != 3) {
            return Optional.empty();
        }
        return Optional.ofNullable(runtimes.get(split[2]));
    }

    /**
     * Gets an {@link Optional} holding the {@link InteractionRuntime}. Returns an empty {@link Optional} if no
     * {@link InteractionRuntime} has been created yet by calling
     * {@link #newRuntime(GenericCommandInteractionEvent, GenericInteractionDefinition)}, if the underlying component wasn't
     * created by jda-commands or if the {@link InteractionRuntime} expired.
     *
     * @param event the {@link ModalInteractionEvent} to get the {@link InteractionRuntime} for
     * @return an {@link Optional} holding the {@link InteractionRuntime}
     */
    public Optional<InteractionRuntime> getRuntime(ModalInteractionEvent event) {
        String[] split = event.getModalId().split("\\.");
        if (split.length != 3) {
            return Optional.empty();
        }
        return Optional.ofNullable(runtimes.get(split[2]));
    }

    /**
     * A runtime used for executing interactions. This class holds the instance of the class annotated with
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} where commands,
     * buttons, etc. live in.
     *
     * @since 4.0.0
     */
    public static class InteractionRuntime {
        private final String instanceId;
        private final Object instance;
        private MessageCreateData messageCreateData;

        /**
         * Constructs a new InteractionRuntime.
         *
         * @param instanceId the id of this instance, i.e. the snowflake id of the event creating this runtime
         * @param instance   the instance of the
         *                   {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} class
         */
        public InteractionRuntime(String instanceId, Object instance) {
            this.instanceId = instanceId;
            this.instance = instance;
        }

        /**
         * Gets the instance id.
         *
         * @return the instance id
         */
        public String getInstanceId() {
            return instanceId;
        }

        /**
         * Gets the instance.
         *
         * @return the instance
         */
        public Object getInstance() {
            return instance;
        }

        /**
         * Gets the latest message that was sent with this runtime.
         *
         * @return an {@link Optional} holding a {@link MessageCreateData} describing the latest message
         */
        public Optional<MessageCreateData> getLatestReply() {
            return Optional.ofNullable(messageCreateData);
        }

        /**
         * Sets the latest message sent with this runtime.
         *
         * @param messageCreateData a {@link MessageCreateData} describing the latest message
         */
        public void setLatestReply(@Nullable MessageCreateData messageCreateData) {
            this.messageCreateData = messageCreateData;
        }
    }
}
