package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.StaticInstance;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.GenericComponentDefinition;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
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

import static com.github.kaktushose.jda.commands.reflect.interactions.CustomId.CUSTOM_ID_REGEX;

/**
 * Supervisor that creates and stores {@link InteractionRuntime InteractionRuntimes}. This supervisor will create a
 * new {@link InteractionRuntime} for every command execution with a TTL of 15 minutes.
 *
 * @since 4.0.0
 */
public class RuntimeSupervisor {

    private final Map<String, InteractionRuntime> runtimes;
    private final ScheduledExecutorService executor;
    private final DependencyInjector injector;

    /**
     * Constructs a new RuntimeSupervisor.
     */
    public RuntimeSupervisor(DependencyInjector injector) {
        this.injector = injector;
        runtimes = new HashMap<>();
        executor = new ScheduledThreadPoolExecutor(4);
    }

    /**
     * Creates a new {@link InteractionRuntime}, which will expire after 15 minutes. If the interaction class is instead
     * marked with {@link StaticInstance} will instead return the static instance.
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

        Class<?> interactionClass = interaction.getMethod().getDeclaringClass();
        if (interactionClass.isAnnotationPresent(StaticInstance.class)) {
            String runtimeId = String.format("s%s", interactionClass.getName().hashCode());
            if (runtimes.containsKey(runtimeId)) {
                return runtimes.get(runtimeId);
            }

            Object instance = interaction.newInstance();
            injector.inject(instance);

            InteractionRuntime runtime = new InteractionRuntime(runtimeId, instance);
            runtimes.put(runtimeId, runtime);
            return runtime;
        }

        Object instance = interaction.newInstance();
        injector.inject(instance);

        String id = event.getId();
        InteractionRuntime runtime = new InteractionRuntime(id, instance);

        runtimes.put(id, runtime);
        executor.schedule(() -> runtimes.remove(id), 15, TimeUnit.MINUTES);

        return runtime;
    }

    /**
     * Gets the static runtime for the given {@link AutoCompleteDefinition}. If no instance exists yet, creates, stores and then
     * returns the instance.
     *
     * @param autoComplete the {@link GenericInteractionDefinition} to create or get an instance from
     * @return an instance of the provided {@link GenericInteractionDefinition}
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException    if this Constructor object is enforcing Java language access control and
     *                                   the underlying constructor is inaccessible
     */
    public InteractionRuntime newRuntime(AutoCompleteDefinition autoComplete)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String runtimeId = String.format("s%s", autoComplete.getMethod().getDeclaringClass().getName().hashCode());
        if (runtimes.containsKey(runtimeId)) {
            return runtimes.get(runtimeId);
        }

        Object instance = autoComplete.newInstance();
        injector.inject(instance);

        InteractionRuntime runtime = new InteractionRuntime(runtimeId, instance);
        runtimes.put(runtimeId, runtime);
        return runtime;
    }

    /**
     * Creates a new runtime for the given {@link GenericComponentDefinition}. This runtime will always be static.
     *
     * @param componentDefinition the {@link GenericComponentDefinition} to create the runtime for
     */
    public InteractionRuntime newRuntime(GenericComponentDefinition componentDefinition) {
        Class<?> interactionClass = componentDefinition.getMethod().getDeclaringClass();
        String runtimeId = String.format("s%s", interactionClass.getName().hashCode());
        if (runtimes.containsKey(runtimeId)) {
            return runtimes.get(runtimeId);
        }

        Object instance;
        try {
            instance = componentDefinition.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        injector.inject(instance);

        InteractionRuntime runtime = new InteractionRuntime(runtimeId, instance);
        runtimes.put(runtimeId, runtime);

        return runtime;
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
        return getRuntime(event.getComponentId());
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
        return getRuntime(event.getModalId());
    }

    private Optional<InteractionRuntime> getRuntime(String interactionId) {
        if (!interactionId.matches(CUSTOM_ID_REGEX)) {
            return Optional.empty();
        }
        String runtimeId = interactionId.split("\\.")[2];
        return Optional.ofNullable(runtimes.get(runtimeId));
    }

    /**
     * A runtime used for executing interactions. This class holds the instance of the class annotated with
     * {@link Interaction Interaction} where commands, buttons, etc. live in. This runtime can only be used once per
     * command execution.
     *
     * @since 4.0.0
     */
    public static class InteractionRuntime {
        private final String runtimeId;
        private final Object instance;
        private MessageCreateData messageCreateData;
        private KeyValueStore keyValueStore;

        /**
         * Constructs a new InteractionRuntime.
         *
         * @param runtimeId the id of this instance, i.e. the snowflake id of the event creating this runtime
         * @param instance  the instance of the {@link Interaction Interaction} class
         */
        public InteractionRuntime(String runtimeId, Object instance) {
            this.runtimeId = runtimeId;
            this.instance = instance;
            keyValueStore = new KeyValueStore();
        }

        /**
         * Gets the instance id.
         *
         * @return the instance id
         */
        public String getRuntimeId() {
            return runtimeId;
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

        /**
         * Gets the {@link KeyValueStore} that is bound to this runtime.
         *
         * @return the {@link KeyValueStore} bound to this runtime
         */
        public KeyValueStore getKeyValueStore() {
            return keyValueStore;
        }

        /**
         * Sets the {@link KeyValueStore} that is bound to this runtime.
         *
         * @param keyValueStore the {@link KeyValueStore} bound to this runtime
         */
        public void setKeyValueStore(KeyValueStore keyValueStore) {
            this.keyValueStore = keyValueStore;
        }
    }
}
