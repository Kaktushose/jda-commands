package com.github.kaktushose.jda.commands.dispatching.interactions.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides additional features for sending messages or editing the original message and also grants
 * access to the {@link ButtonDefinition} object which describes the button interaction that is executed.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see GenericEvent
 * @see Replyable
 * @since 1.0.0
 */
public class ButtonEvent extends GenericEvent implements Replyable {

    private final ButtonDefinition button;
    private final ButtonContext context;
    private final ReplyContext replyContext;

    /**
     * Constructs a ButtonEvent.
     *
     * @param button  the underlying {@link ButtonDefinition} object
     * @param context the {@link ButtonContext}
     */
    public ButtonEvent(@NotNull ButtonDefinition button, @NotNull ButtonContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.button = button;
        this.context = context;
        replyContext = new ReplyContext(context);
    }

    /**
     * Get the {@link ButtonDefinition} object which describes the button that is executed.
     *
     * @return the underlying {@link ButtonDefinition} object
     */
    public ButtonDefinition getButtonDefinition() {
        return button;
    }

    /**
     * Get the {@link JDACommands} object.
     *
     * @return the {@link JDACommands} object
     */
    public JDACommands getJdaCommands() {
        return context.getJdaCommands();
    }

    /**
     * Get the {@link ButtonContext} object.
     *
     * @return the registered {@link ButtonContext} object
     */
    @Override
    public ButtonContext getContext() {
        return context;
    }

    @Override
    public @NotNull ReplyContext getReplyContext() {
        return replyContext;
    }

    @Override
    public void reply() {
        Optional<MessageCreateData> optional = context.getRuntime().getLatestReply();
        if (optional.isPresent()) {
            MessageCreateData cached = optional.get();
            if (replyContext.isKeepComponents() && replyContext.getBuilder().getComponents().isEmpty()) {
                replyContext.getBuilder().setComponents(cached.getComponents());
            }

        }
        replyContext.queue();
        context.getRuntime().setLatestReply(replyContext.toMessageCreateData());
    }
}
