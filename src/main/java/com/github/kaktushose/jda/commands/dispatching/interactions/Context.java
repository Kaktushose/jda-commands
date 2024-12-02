package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class models an interaction execution. It gets constructed at the beginning of dispatching and gets passed
 * through the execution chain.
 *
 * @see GenericEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext SlashCommandContext
 * @since 2.0.0
 */
public class Context {

    protected final GenericInteractionCreateEvent event;
    protected MessageCreateData errorMessage;
    protected ImplementationRegistry registry;
    protected boolean cancelled;
    protected boolean ephemeral;
    protected InteractionRuntime runtime;
    protected GenericInteractionDefinition interactionDefinition;
    protected InteractionRegistry interactionRegistry;
    protected ImplementationRegistry implementationRegistry;

    /**
     * Constructs a new GenericContext.
     *
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     */
    public Context(GenericInteractionCreateEvent event) {
        this.event = event;
    }

    public GenericInteractionCreateEvent getEvent() {
        return event;
    }

    /**
     * Gets the {@link Message} to send if an error occurred.
     *
     * @return {@link Message} to send
     */
    @Nullable
    public MessageCreateData getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the {@link ImplementationRegistry} instance.
     *
     * @param registry the {@link ImplementationRegistry} instance
     * @return the current CommandContext instance
     */
    @NotNull
    public Context setImplementationRegistry(@NotNull ImplementationRegistry registry) {
        this.registry = registry;
        return this;
    }

    /**
     * Whether the context should be cancelled.
     *
     * @return {@code true} if the context should be cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets this context as cancelled and uses the provided {@link MessageCreateData} as an error message.
     *
     * @param errorMessage the error message as {@link MessageCreateData}
     * @return the current Context instance
     */
    public Context setCancelled(MessageCreateData errorMessage) {
        this.cancelled = true;
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * Undoes {@link #setCancelled(MessageCreateData)} and.
     *
     * @return the current Context instance
     */
    public Context setUncancelled() {
        this.cancelled = false;
        return this;
    }

    /**
     * Whether this context should send ephemeral replies.
     *
     * @return {@code true} if this context should send ephemeral replies
     */
    public boolean isEphemeral() {
        return ephemeral;
    }


    /**
     * Sets whether this context should send ephemeral replies.
     *
     * @param ephemeral {@code true} if this context should send ephemeral replies
     * @return the current Context instance
     */
    public Context setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
        return this;
    }

    /**
     * Gets the {@link InteractionRuntime} used to execute this command event.
     *
     * @return the {@link InteractionRuntime}
     */
    public InteractionRuntime getRuntime() {
        return runtime;
    }

    /**
     * Sets the {@link InteractionRuntime} that will be used to execute this command event.
     *
     * @return the current CommandContext instance
     */
    public Context setRuntime(InteractionRuntime runtime) {
        this.runtime = runtime;
        return this;
    }

    /**
     * Gets the {@link GenericInteractionDefinition} this context got created from.
     *
     * @return the {@link GenericInteractionDefinition}
     */
    public GenericInteractionDefinition getInteractionDefinition() {
        return interactionDefinition;
    }

    /**
     * Sets the {@link GenericInteractionDefinition}
     *
     * @param interactionDefinition the {@link GenericInteractionDefinition} of this context
     * @return the current Context instance
     */
    public Context setInteractionDefinition(GenericInteractionDefinition interactionDefinition) {
        this.interactionDefinition = interactionDefinition;
        return this;
    }

    /**
     * Gets the {@link KeyValueStore} that is bound to this runtime.
     *
     * @return the {@link KeyValueStore} bound to this runtime
     */
    public KeyValueStore getKeyValueStore() {
        return runtime.getKeyValueStore();
    }

    /**
     * Sets the {@link KeyValueStore} that is bound to this runtime.
     *
     * @param keyValueStore the {@link KeyValueStore} bound to this runtime
     * @return the current Context instance
     */
    public Context setKeyValueStore(KeyValueStore keyValueStore) {
        runtime.setKeyValueStore(keyValueStore);
        return this;
    }
}
