package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.internal.CommandDispatcher;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This class is used to represent an active instance of this framework and provides access to the {@link CommandSettings}
 * and the {@link CommandList}.
 *
 * <p>It is basically an abstraction of {@link CommandDispatcher} in order to filter the access to methods and attributes.
 * An instance of this class may only be created through the {@link JDACommandsBuilder} due to the high
 * complexity of {@link CommandDispatcher}.
 *
 * @author Kaktushose
 * @version 1.1.0
 * @see JDACommandsBuilder
 * @since 1.0.0
 */
public class JDACommands {

    private final CommandDispatcher commandDispatcher;

    /**
     * Constructs a JDACommands. The {@link JDACommandsBuilder} may be used instead of this constructor.
     *
     * @param commandDispatcher the {@link CommandDispatcher} instance to start the framework with
     * @see JDACommandsBuilder
     */
    public JDACommands(@Nonnull CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
        commandDispatcher.start(this);
    }

    /**
     * Get the default {@link CommandSettings}.
     *
     * @return the {@link CommandSettings} of this instance
     */
    public CommandSettings getDefaultSettings() {
        return commandDispatcher.getDefaultSettings();
    }

    /**
     * Get the {@link CommandSettings}s that are mapped to a guild. This Map is mutable and thus can be modified in
     * order to add or remove guild settings. Returns an empty Map if no guild settings have been defined yet.
     *
     * @return a mutable Map containing all the guild specific {@link CommandSettings}s
     */
    public Map<Long, CommandSettings> getGuildSettings() {
        return commandDispatcher.getGuildSettings();
    }


    /**
     * Get the {@link CommandList} of the current framework instance.
     *
     * @return the {@link CommandList} of this instance
     */
    public CommandList getCommands() {
        return commandDispatcher.getCommands();
    }

    /**
     * Shuts down the current framework instance. This will unregister the message listener and clear the {@link CommandList}.
     * Thus it is possible to build a new JDACommands instance after this method got invoked.
     */
    public void shutdown() {
        commandDispatcher.shutdown();
    }

}
