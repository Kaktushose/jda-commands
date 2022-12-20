package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.sender.EditAction;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyAction;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionReplyCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.TextReplyCallback;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.interactions.components.Buttons;
import com.github.kaktushose.jda.commands.interactions.components.Component;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final List<ActionRow> actionRows;
    private ReplyCallback replyCallback;

    /**
     * Constructs a CommandEvent.
     *
     * @param command the underlying {@link CommandDefinition} object
     * @param context the {@link CommandContext}
     */
    @SuppressWarnings("ConstantConditions")
    public CommandEvent(@NotNull CommandDefinition command, @NotNull CommandContext context) {
        super(context.getEvent());
        this.command = command;
        this.context = context;
        actionRows = new ArrayList<>();
        if (context.isSlash()) {
            replyCallback = new InteractionReplyCallback(context.getInteractionEvent(), actionRows);
        } else {
            replyCallback = new TextReplyCallback(getChannel(), actionRows);
        }
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

    @SuppressWarnings("ConstantConditions")
    public CommandEvent with(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {
            if (!(component instanceof Buttons)) {
                return this;
            }
            Buttons buttons = (Buttons) component;
            buttons.getButtons().forEach(button -> {
                String id = String.format("%s.%s", command.getMethod().getDeclaringClass().getSimpleName(), button.getId());
                command.getController().getButtons()
                        .stream()
                        .filter(it -> it.getId().equals(id))
                        .findFirst()
                        .map(it -> it.toButton().withDisabled(!button.isEnabled()))
                        .ifPresent(items::add);
            });
        }
        if (items.size() > 0) {
            actionRows.add(ActionRow.of(items));
        }
        return this;
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
     * Get the {@link CommandContext} object.
     *
     * @return the registered {@link CommandContext} object
     */
    public CommandContext getCommandContext() {
        return context;
    }

    /**
     * Gets the {@link InteractionHook}. The {@link InteractionHook} is only available if the underlying event was a
     * {@link net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent SlashCommandInteractionEvent}.
     *
     * @return an {@link Optional} holding the {@link InteractionHook}.
     */
    public Optional<InteractionHook> getInteractionHook() {
        return Optional.ofNullable(context.getInteractionEvent()).map(GenericCommandInteractionEvent::getHook);
    }

    /**
     * Gets the {@link ActionRow ActionRows} already added to the reply.
     *
     * @return a possibly-empty {@link List} of {@link ActionRow ActionRows}.
     */
    public List<ActionRow> getActionRows() {
        return new ArrayList<>(actionRows);
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
