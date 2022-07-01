package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.EditCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Implementation of {@link EditCallback} that can handle interaction events. More formally, this callback can handle
 * any event that is a subtype of {@link IMessageEditCallback}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see EditCallback
 * @since 2.3.0
 */
public class InteractionEditCallback implements EditCallback {

    private final IMessageEditCallback event;
    private final Collection<ActionRow> actionRows;
    private boolean initialReply;

    /**
     * Constructs a new {@link ReplyCallback}.
     *
     * @param event      the corresponding event
     * @param actionRows a {@link Collection} of {@link ActionRow ActionRows to send}
     */
    public InteractionEditCallback(IMessageEditCallback event, Collection<ActionRow> actionRows) {
        this.event = event;
        this.actionRows = actionRows;
        initialReply = false;
    }

    @Override
    public void editMessage(@NotNull String message, @Nullable Consumer<Message> success) {
        initialReply(hook -> send(hook.editOriginal(message), success));
    }

    @Override
    public void editMessage(@NotNull Message message, @Nullable Consumer<Message> success) {
        initialReply(hook -> send(hook.editOriginal(message), success));
    }

    @Override
    public void editMessage(@NotNull MessageEmbed embed, @Nullable Consumer<Message> success) {
        initialReply(hook -> send(hook.editOriginalEmbeds(embed), success));
    }

    @Override
    public void deleteOriginal() {
        initialReply(hook -> hook.deleteOriginal().queue());
    }

    @Override
    public void editComponents(@NotNull LayoutComponent @NotNull ... components) {
        initialReply(hook -> hook.editOriginalComponents(components).queue());
    }

    private void send(WebhookMessageUpdateAction<Message> restAction, Consumer<Message> success) {
        if (actionRows.size() > 0) {
            restAction.setActionRows(actionRows).queue(success);
        }
        restAction.queue(success);
    }

    private void initialReply(Consumer<InteractionHook> consumer) {
        if (!initialReply) {
            initialReply = true;
            event.deferEdit().queue(consumer);
            return;
        }
        consumer.accept(event.getHook());
    }

}
