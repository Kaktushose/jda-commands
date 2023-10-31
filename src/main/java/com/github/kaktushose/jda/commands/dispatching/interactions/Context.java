package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
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
 * @author Kaktushose
 * @version 4.0.0
 * @see GenericEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext SlashCommandContext
 * @since 2.0.0
 */
public class Context {

    protected final GenericInteractionCreateEvent event;
    protected MessageCreateData errorMessage;
    protected ImplementationRegistry registry;
    protected JDACommands jdaCommands;
    protected boolean cancelled;
    protected boolean ephemeral;
    protected InteractionRuntime runtime;
    protected GenericInteractionDefinition interactionDefinition;

    /**
     * Constructs a new GenericContext.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     */
    public Context(GenericInteractionCreateEvent event, JDACommands jdaCommands) {
        this.event = event;
        this.jdaCommands = jdaCommands;
        this.registry = jdaCommands.getImplementationRegistry();
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
     * Set the {@link Message} to send if an error occurred.
     *
     * @param message the {@link Message} to send
     * @return the current CommandContext instance
     */
    @NotNull
    public Context setErrorMessage(@NotNull MessageCreateData message) {
        this.errorMessage = message;
        return this;
    }

    /**
     * Gets the corresponding {@link ImplementationRegistry} instance.
     *
     * @return the corresponding {@link ImplementationRegistry} instance
     */
    @NotNull
    public ImplementationRegistry getImplementationRegistry() {
        return registry;
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
     * Gets the corresponding {@link JDACommands} instance.
     *
     * @return the corresponding {@link JDACommands} instance
     */
    @NotNull
    public JDACommands getJdaCommands() {
        return jdaCommands;
    }

    /**
     * Set the {@link JDACommands} instance.
     *
     * @param jdaCommands the {@link JDACommands} instance
     * @return the current CommandContext instance
     */
    @NotNull
    public Context setJdaCommands(@NotNull JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
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
     * Set whether the context should be cancelled.
     *
     * @param cancelled whether the context should be cancelled
     * @return the current CommandContext instance
     */
    @NotNull
    public Context setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

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

    public GenericInteractionDefinition getInteractionDefinition() {
        return interactionDefinition;
    }

    public Context setInteractionDefinition(GenericInteractionDefinition interactionDefinition) {
        this.interactionDefinition = interactionDefinition;
        return this;
    }
}
