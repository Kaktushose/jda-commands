package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.github.kaktushose.jda.commands.slash.GenericCommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a command execution. The
 * {@link com.github.kaktushose.jda.commands.dispatching.parser.Parser Parser} constructs a new CommandContext for each
 * valid event received. The CommandContext is then passed through the execution chain until it is then transformed into
 * a {@link CommandEvent}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class CommandContext {

    private String[] input;
    private GenericCommandEvent event;
    private CommandDefinition command;
    private List<CommandDefinition> possibleCommands;
    private List<Object> arguments;
    private Message errorMessage;
    private GuildSettings settings;
    private ImplementationRegistry registry;
    private JDACommands jdaCommands;
    private boolean isHelpEvent;
    private boolean cancelled;

    /**
     * Gets the raw user input.
     *
     * @return the raw user input
     */
    public String[] getInput() {
        return input;
    }

    /**
     * Set the user input.
     *
     * @param input the user input
     * @return the current CommandContext instance
     */
    public CommandContext setInput(@NotNull String[] input) {
        this.input = input;
        return this;
    }

    /**
     * Gets the corresponding {@link MessageReceivedEvent}.
     *
     * @return the corresponding {@link MessageReceivedEvent}
     */
    public GenericCommandEvent getEvent() {
        return event;
    }

    /**
     * Set the {@link MessageReceivedEvent}.
     *
     * @param event the {@link MessageReceivedEvent}
     * @return the current CommandContext instance
     */
    public CommandContext setEvent(@NotNull MessageReceivedEvent event) {
        this.event = GenericCommandEvent.fromEvent(event);
        return this;
    }

    /**
     * Set the {@link SlashCommandInteractionEvent}.
     *
     * @param event the {@link SlashCommandInteractionEvent}
     * @return the current CommandContext instance
     */
    public CommandContext setEvent(@NotNull SlashCommandInteractionEvent event) {
        this.event = GenericCommandEvent.fromEvent(event);
        return this;
    }

    /**
     * Gets the {@link CommandDefinition}.
     *
     * @return the {@link CommandDefinition}
     */
    public CommandDefinition getCommand() {
        return command;
    }

    /**
     * Set the {@link CommandDefinition}.
     *
     * @param command the {@link CommandDefinition}
     * @return the current CommandContext instance
     */
    public CommandContext setCommand(@Nullable CommandDefinition command) {
        this.command = command;
        return this;
    }

    /**
     * Gets a list of {@link CommandDefinition CommandDefinitions} that match the input. This list gets populated if
     * and only if the command routing fails because more than one matching command was found. If no matching command
     * was found or if the command routing succeeded, this list will be empty.
     *
     * @return a possibly-empty list of {@link CommandDefinition CommandDefinitions} that match the input
     */
    public List<CommandDefinition> getPossibleCommands() {
        return possibleCommands == null ? new ArrayList<>() : possibleCommands;
    }

    /**
     * Sets the list of {@link CommandDefinition CommandDefinitions} that match the input.
     *
     * @param possibleCommands a list of {@link CommandDefinition CommandDefinitions} that match the input
     * @return the current CommandContext instance
     */
    public CommandContext setPossibleCommands(@NotNull List<CommandDefinition> possibleCommands) {
        this.possibleCommands = possibleCommands;
        return this;
    }

    /**
     * Gets the parsed arguments.
     *
     * @return the parsed arguments
     */
    public List<Object> getArguments() {
        return arguments;
    }

    /**
     * Set the arguments.
     *
     * @param arguments the parsed arguments
     * @return the current CommandContext instance
     */
    public CommandContext setArguments(@NotNull List<Object> arguments) {
        this.arguments = arguments;
        return this;
    }

    /**
     * Gets the {@link Message} to send if an error occurred.
     *
     * @return {@link Message} to send
     */
    public Message getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the the {@link Message} to send if an error occurred.
     *
     * @param message the {@link Message} to send
     * @return the current CommandContext instance
     */
    public CommandContext setErrorMessage(@NotNull Message message) {
        this.errorMessage = message;
        return this;
    }

    /**
     * Gets the corresponding {@link GuildSettings}.
     *
     * @return the corresponding {@link GuildSettings}
     */
    public GuildSettings getSettings() {
        return settings;
    }

    /**
     * Set the {@link GuildSettings}.
     *
     * @param settings the {@link GuildSettings}
     * @return the current CommandContext instance
     */
    public CommandContext setSettings(@NotNull GuildSettings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Gets the corresponding {@link ImplementationRegistry} instance.
     *
     * @return the corresponding {@link ImplementationRegistry} instance
     */
    public ImplementationRegistry getImplementationRegistry() {
        return registry;
    }

    /**
     * Set the {@link ImplementationRegistry} instance.
     *
     * @param registry the {@link ImplementationRegistry} instance
     * @return the current CommandContext instance
     */
    public CommandContext setImplementationRegistry(@NotNull ImplementationRegistry registry) {
        this.registry = registry;
        return this;
    }

    /**
     * Gets the corresponding {@link JDACommands} instance.
     *
     * @return the corresponding {@link JDACommands} instance
     */
    public JDACommands getJdaCommands() {
        return jdaCommands;
    }

    /**
     * Set the {@link JDACommands} instance.
     *
     * @param jdaCommands the {@link JDACommands} instance
     * @return the current CommandContext instance
     */
    public CommandContext setJdaCommands(@NotNull JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
        return this;
    }

    /**
     * Whether the context represents a help event.
     *
     * @return {@code true} if the context represents a help event
     */
    public boolean isHelpEvent() {
        return isHelpEvent;
    }

    /**
     * Set whether the context represents a help event.
     *
     * @param helpEvent whether the context represents a help event
     * @return the current CommandContext instance
     */
    public CommandContext setHelpEvent(boolean helpEvent) {
        isHelpEvent = helpEvent;
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
    public CommandContext setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }
}
