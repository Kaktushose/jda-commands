package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.sender.MessageSender;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Default implementation of {@link MessageSender}.
 *
 * @author Kaktushose
 * @version 2.1.0
 * @see MessageSender
 * @since 2.1.0
 */
public class DefaultMessageSender implements MessageSender {

    @Override
    public void sendGenericHelpMessage(@NotNull CommandContext context, @NotNull Message message) {
        if (context.isSlash()) {
            context.getInteractionEvent().reply(message).queue();
        } else {
            context.getEvent().getChannel().sendMessage(message).queue();
        }
    }

    @Override
    public void sendSpecificHelpMessage(@NotNull CommandContext context, @NotNull Message message) {
        if (context.isSlash()) {
            context.getInteractionEvent().reply(message).queue();
        } else {
            context.getEvent().getChannel().sendMessage(message).queue();
        }
    }

    @Override
    public void sendErrorMessage(@NotNull CommandContext context, @NotNull Message message) {
        if (context.isSlash()) {
            context.getInteractionEvent().reply(message).queue();
        } else {
            context.getEvent().getChannel().sendMessage(message).queue();
        }
    }

}
