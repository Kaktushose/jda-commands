package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.dispatching.reply.Replyable;
import com.github.kaktushose.jda.commands.dispatching.reply.impl.CommandReplyCallback;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.interactions.components.Buttons;
import com.github.kaktushose.jda.commands.interactions.components.Component;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.interactions.components.ActionRow;
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
 * @see Replyable
 * @since 1.0.0
 */
public class CommandEvent extends GenericEvent implements Replyable {

    private final CommandDefinition command;
    private final CommandContext context;
    private final ReplyContext replyContext;
    private final CommandReplyCallback replyCallback;

    /**
     * Constructs a CommandEvent.
     *
     * @param command the underlying {@link CommandDefinition} object
     * @param context the {@link GenericContext}
     */
    public CommandEvent(@NotNull CommandDefinition command, @NotNull CommandContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        this.command = command;
        this.context = context;
        replyContext = new ReplyContext();
        replyCallback = new CommandReplyCallback(context);
    }

    /**
     * Sends the generic help message via the
     * {@link com.github.kaktushose.jda.commands.dispatching.reply.MessageSender MessageSender}
     */
    public void sendGenericHelpMessage() {
//        getJdaCommands().getImplementationRegistry().getMessageSender().sendGenericHelpMessage(
//                context,
//                getHelpMessageFactory().getGenericHelp(getJdaCommands().getCommandRegistry().getControllers(), context)
//        );
    }

    /**
     * Sends the specific help message for this command via the
     * {@link com.github.kaktushose.jda.commands.dispatching.reply.MessageSender MessageSender}
     */
    public void sendSpecificHelpMessage() {
//        getJdaCommands().getImplementationRegistry().getMessageSender().sendSpecificHelpMessage(
//                context,
//                getHelpMessageFactory().getSpecificHelp(context)
//        );
    }

//    /**
//     * Replies to this event with the generic help embed.
//     */
//    public void replyGenericHelp() {
//        reply(getHelpMessageFactory().getGenericHelp(getJdaCommands().getCommandRegistry().getControllers(), context));
//    }
//
//    /**
//     * Replies to this event with the specific help embed.
//     */
//    public void replySpecificHelp() {
//        reply(getHelpMessageFactory().getSpecificHelp(context));
//    }
//
//    /**
//     * Replies to this event with the generic help embed.
//     *
//     * @param ephemeral whether to send an ephemeral reply
//     */
//    public void replyGenericHelp(boolean ephemeral) {
//        reply(getHelpMessageFactory().getGenericHelp(getJdaCommands().getCommandRegistry().getControllers(), context), ephemeral);
//    }
//
//    /**
//     * Replies to this event with the specific help embed.
//     *
//     * @param ephemeral whether to send an ephemeral reply
//     */
//    public void replySpecificHelp(boolean ephemeral) {
//        reply(getHelpMessageFactory().getSpecificHelp(context), ephemeral);
//    }

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
    public Replyable with(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof Buttons) {
                Buttons buttons = (Buttons) component;
                buttons.getButtons().forEach(button -> {
                    String id = String.format("%s.%s", command.getMethod().getDeclaringClass().getSimpleName(), button.getId());
                    getJdaCommands().getInteractionRegistry().getButtons()
                            .stream()
                            .filter(it -> it.getId().equals(id))
                            .findFirst()
                            .map(it -> it.toButton().withDisabled(!button.isEnabled()))
                            .ifPresent(items::add);
                });
            }
        }
        if (items.size() > 0) {
            getReplyContext().getBuilder().addComponents(ActionRow.of(items));
        }
        return this;
    }

    @Override
    public ReplyContext getReplyContext() {
        return replyContext.setEphemeralReply(command.isEphemeral());
    }

    @Override
    public void reply() {
        replyCallback.sendMessage(replyContext);
    }
}
