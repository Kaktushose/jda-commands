package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.sender.EditAction;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyAction;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionReplyCallback;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.interactions.components.Component;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a subclass of {@link GenericEvent}.
 * It provides some additional features for sending messages and also grants
 * access to the {@link CommandDefinition} object which describes the command that is executed.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see GenericEvent
 * @see ReplyAction
 * @see EditAction
 * @since 1.0.0
 */
public class CommandEvent extends GenericEvent implements ReplyAction {

    private final CommandDefinition command;
    private final CommandContext context;
    private final List<ItemComponent> actionRows;
    private ReplyCallback replyCallback;

    /**
     * Constructs a CommandEvent.
     *
     * @param command the underlying {@link CommandDefinition} object
     * @param context the {@link GenericContext}
     */
    @SuppressWarnings("ConstantConditions")
    public CommandEvent(@NotNull CommandDefinition command, @NotNull CommandContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.command = command;
        this.context = context;
        actionRows = new ArrayList<>();
        replyCallback = new InteractionReplyCallback(context.getEvent(), actionRows);
    }

    /**
     * Sends the generic help message via the
     * {@link com.github.kaktushose.jda.commands.dispatching.sender.MessageSender MessageSender}
     */
    public void sendGenericHelpMessage() {
        getJdaCommands().getImplementationRegistry().getMessageSender().sendGenericHelpMessage(
                context,
                getHelpMessageFactory().getGenericHelp(getJdaCommands().getCommandRegistry().getControllers(), context)
        );
    }

    /**
     * Sends the specific help message for this command via the
     * {@link com.github.kaktushose.jda.commands.dispatching.sender.MessageSender MessageSender}
     */
    public void sendSpecificHelpMessage() {
        getJdaCommands().getImplementationRegistry().getMessageSender().sendSpecificHelpMessage(
                context,
                getHelpMessageFactory().getSpecificHelp(context)
        );
    }

    /**
     * Replies to this event with the generic help embed.
     */
    public void replyGenericHelp() {
        reply(getHelpMessageFactory().getGenericHelp(getJdaCommands().getCommandRegistry().getControllers(), context));
    }

    /**
     * Replies to this event with the specific help embed.
     */
    public void replySpecificHelp() {
        reply(getHelpMessageFactory().getSpecificHelp(context));
    }

    /**
     * Replies to this event with the generic help embed.
     *
     * @param ephemeral whether to send an ephemeral reply
     */
    public void replyGenericHelp(boolean ephemeral) {
        reply(getHelpMessageFactory().getGenericHelp(getJdaCommands().getCommandRegistry().getControllers(), context), ephemeral);
    }

    /**
     * Replies to this event with the specific help embed.
     *
     * @param ephemeral whether to send an ephemeral reply
     */
    public void replySpecificHelp(boolean ephemeral) {
        reply(getHelpMessageFactory().getSpecificHelp(context), ephemeral);
    }

    /**
     * Get the {@link CommandDefinition} object which describes the command that is executed.
     *
     * @return the underlying {@link CommandDefinition} object
     */
    public CommandDefinition getCommandDefinition() {
        return command;
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
     * Get the registered {@link HelpMessageFactory} object.
     *
     * @return the registered {@link HelpMessageFactory} object
     */
    public HelpMessageFactory getHelpMessageFactory() {
        return getJdaCommands().getImplementationRegistry().getHelpMessageFactory();
    }

    /**
     * Get the {@link GenericContext} object.
     *
     * @return the registered {@link GenericContext} object
     */
    public CommandContext getCommandContext() {
        return context;
    }

    @Override
    public ReplyAction with(@NotNull Component... components) {
        return null;
    }

    @Override
    public @NotNull ReplyCallback getReplyCallback() {
        return replyCallback;
    }

    /**
     * Sets the {@link ReplyCallback} used to send replies to this event.
     *
     * @param replyCallback the {@link ReplyCallback} to use
     */
    public void setReplyCallback(ReplyCallback replyCallback) {
        this.replyCallback = replyCallback;
    }

    @Override
    public boolean isEphemeral() {
        return command.isEphemeral();
    }
}
