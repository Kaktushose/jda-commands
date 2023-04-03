package com.github.kaktushose.jda.commands.dispatching.reply.impl;

import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Reply callback that can handle slash command interactions.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class CommandReplyCallback {

    private final SlashCommandInteractionEvent event;

    /**
     * Constructs a new CommandReplyCallback.
     *
     * @param context the corresponding {@link CommandContext}
     */
    public CommandReplyCallback(@NotNull CommandContext context) {
        this.event = context.getEvent();
    }

    public void sendMessage(ReplyContext context) {
        event.getHook().setEphemeral(context.isEphemeralReply());
        if (context.isEditReply()) {
            event.getHook().editOriginal(context.toMessageEditData()).queue(context.getConsumer());
            return;
        }
        event.getHook().sendMessage(context.toMessageCreateData()).queue(context.getConsumer());
    }

}
