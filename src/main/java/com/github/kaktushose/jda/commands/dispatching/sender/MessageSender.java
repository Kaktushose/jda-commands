package com.github.kaktushose.jda.commands.dispatching.sender;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import net.dv8tion.jda.api.entities.Message;
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
     * @param context the corresponding {@link GenericContext}
     * @param message the help message to send
     */
    void sendGenericHelpMessage(@NotNull GenericContext context, @NotNull Message message);

    /**
     * Called when a specific help message should be sent.
     *
     * @param context the corresponding {@link GenericContext}
     * @param message the help message to send
     */
    void sendSpecificHelpMessage(@NotNull GenericContext context, @NotNull Message message);

    /**
     * Called when an error message should be sent.
     *
     * @param context the corresponding {@link GenericContext}
     * @param message the error message to send
     */
    void sendErrorMessage(@NotNull GenericContext context, @NotNull MessageCreateData message);

}
