package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.EditCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
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
        initialReply().editOriginal(message).setActionRows(actionRows).queue(success);
    }

    @Override
    public void editMessage(@NotNull Message message, @Nullable Consumer<Message> success) {
        initialReply().editOriginal(message).setActionRows(actionRows).queue(success);
    }

    @Override
    public void editMessage(@NotNull MessageEmbed embed, @Nullable Consumer<Message> success) {
        initialReply().editOriginalEmbeds(embed).setActionRows(actionRows).queue(success);
    }

    @Override
    public void editComponent(@NotNull LayoutComponent component) {
        initialReply().editOriginalComponents(component).queue();
    }

    @Override
    public void deleteOriginal() {
        initialReply().deleteOriginal().queue();
    }

    private InteractionHook initialReply() {
        if (!initialReply) {
            initialReply = true;
            return event.deferEdit().complete();
        }
        return event.getHook();
    }

}
