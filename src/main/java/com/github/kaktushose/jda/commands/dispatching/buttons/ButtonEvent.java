package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.components.Component;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Constructs a CommandEvent.
     *
     * @param button the underlying {@link ButtonDefinition} object
     * @param context the {@link ButtonContext}
     */
    public ButtonEvent(@NotNull ButtonDefinition button, @NotNull ButtonContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.button = button;
        this.context = context;
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
    public ButtonContext getButtonContext() {
        return context;
    }


    @Override
    public Replyable with(@NotNull Component... components) {
        return null;
    }

    @Override
    public void reply() {

    }

    @Override
    public @NotNull ReplyContext getReplyContext() {
        return null;
    }
}
