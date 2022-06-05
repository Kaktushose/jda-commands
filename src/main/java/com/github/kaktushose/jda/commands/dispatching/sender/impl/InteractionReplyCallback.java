package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Implementation of {@link ReplyCallback} used for {@link SlashCommandInteractionEvent SlashCommandInteractionEvents}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyCallback
 * @see TextReplyCallback
 * @since 2.3.0
 */
public class InteractionReplyCallback implements ReplyCallback {

    private final SlashCommandInteractionEvent event;
    private final Collection<ActionRow> actionRows;
    private boolean initialReply;

    /**
     * Constructs a new {@link ReplyCallback}.
     *
     * @param event      the corresponding {@link SlashCommandInteractionEvent}
     * @param actionRows a {@link Collection} of {@link ActionRow ActionRows to send}
     */
    public InteractionReplyCallback(@NotNull SlashCommandInteractionEvent event, @NotNull Collection<ActionRow> actionRows) {
        this.event = event;
        this.actionRows = actionRows;
        initialReply = false;
    }

    @Override
    public void sendMessage(@NotNull String message, boolean ephemeral, @Nullable Consumer<Message> success) {
        send(initialReply(ephemeral).sendMessage(message), success);
    }

    @Override
    public void sendMessage(@NotNull Message message, boolean ephemeral, @Nullable Consumer<Message> success) {
        send(initialReply(ephemeral).sendMessage(message), success);
    }

    @Override
    public void sendMessage(@NotNull MessageEmbed embed, boolean ephemeral, @Nullable Consumer<Message> success) {
        send(initialReply(ephemeral).sendMessageEmbeds(embed), success);
    }

    private void send(WebhookMessageAction<Message> restAction, Consumer<Message> success) {
        if (actionRows.size() > 0) {
            restAction.addActionRows(actionRows).queue(success);
        } else {
            restAction.queue(success);
        }
    }

    private InteractionHook initialReply(boolean ephemeral) {
        if (!initialReply) {
            initialReply = true;
            return event.deferReply().setEphemeral(ephemeral).complete();
        }
        return event.getHook().setEphemeral(ephemeral);
    }
}
