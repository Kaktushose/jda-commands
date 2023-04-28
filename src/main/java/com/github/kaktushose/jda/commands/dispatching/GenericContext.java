package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class models a command execution. The
 * {@link GenericParser Parser} constructs a new CommandContext for each
 * valid event received. The CommandContext is then passed through the execution chain until it is then transformed into
 * a {@link CommandEvent}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see GenericEvent
 * @since 2.0.0
 */
public class GenericContext<T extends GenericInteractionCreateEvent> {

    protected final T event;
    protected MessageCreateData errorMessage;
    protected ImplementationRegistry registry;
    protected JDACommands jdaCommands;
    protected boolean cancelled;
    protected boolean ephemeral;

    /**
     * Constructs a new CommandContext.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     */
    public GenericContext(T event, JDACommands jdaCommands) {
        this.event = event;
        this.jdaCommands = jdaCommands;
        this.registry = jdaCommands.getImplementationRegistry();
    }

    public T getEvent() {
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
    public GenericContext<? extends GenericInteractionCreateEvent> setErrorMessage(@NotNull MessageCreateData message) {
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
    public GenericContext<? extends GenericInteractionCreateEvent> setImplementationRegistry(@NotNull ImplementationRegistry registry) {
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
    public GenericContext<? extends GenericInteractionCreateEvent> setJdaCommands(@NotNull JDACommands jdaCommands) {
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
    public GenericContext<? extends GenericInteractionCreateEvent> setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

    public GenericContext<? extends GenericInteractionCreateEvent> setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
        return this;
    }
}
