package com.github.kaktushose.jda.commands.dispatching.sender;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.DefaultMessageSender;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

/**
 * Generic interface for sending messages.
 *
 * @author Kaktushose
 * @version 2.1.0
 * @see DefaultMessageSender DefaultHelpMessageSender
 * @since 2.1.0
 */
public interface MessageSender {

    /**
     * Called when a generic help message should be sent.
     *
     * @param context the corresponding {@link CommandContext}
     * @param message the help message to send
     */
    void sendGenericHelpMessage(@NotNull CommandContext context, @NotNull MessageCreateData message);

    /**
     * Called when a specific help message should be sent.
     *
     * @param context the corresponding {@link CommandContext}
     * @param message the help message to send
     */
    void sendSpecificHelpMessage(@NotNull CommandContext context, @NotNull MessageCreateData message);

    /**
     * Called when an error message should be sent.
     *
     * @param context the corresponding {@link CommandContext}
     * @param message the error message to send
     */
    void sendErrorMessage(@NotNull CommandContext context, @NotNull MessageCreateData message);

}
