package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionReplyCallback;
import com.github.kaktushose.jda.commands.dispatching.sender.impl.TextReplyCallback;
import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is a subclass of {@code GenericCommandEvent}.
 * It provides some additional features for sending messages and also grants
 * access to the {@link CommandDefinition} object which describes the command that is executed.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see GenericCommandEvent
 * @since 1.0.0
 */
public class CommandEvent extends GenericCommandEvent {

    private final CommandDefinition commandDefinition;
    private final CommandContext context;
    private final ReplyCallback replyCallback;

    /**
     * Constructs a CommandEvent.
     *
     * @param command the underlying {@link CommandDefinition} object
     * @param context the {@link CommandContext}
     */
    public CommandEvent(@NotNull CommandDefinition command, @NotNull CommandContext context) {
        super(context.getEvent());
        this.commandDefinition = command;
        this.context = context;
        if (context.isSlash()) {
            replyCallback = new InteractionReplyCallback(context.getInteractionEvent());
        } else {
            replyCallback = new TextReplyCallback(getTextChannel());
        }
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param message the message to send
     */
    public void reply(@NotNull String message) {
        reply(message, (Consumer<Message>) null);
    }

    /**
     * Sends a formatted message using the specified format string and arguments to the TextChannel where the command was called.
     *
     * @param format the message to send
     * @param args   Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *               format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *               zero.
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
     *                                          is incompatible with the given arguments, insufficient arguments given
     *                                          the format string, or other illegal conditions.
     */
    public void reply(@NotNull String format, @NotNull Object... args) {
        reply(String.format(format, args));
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param message the {@code Message} to send
     */
    public void reply(@NotNull Message message) {
        reply(message, null);
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param builder the {@code MessageBuilder} to send
     */
    public void reply(@NotNull MessageBuilder builder) {
        reply(builder, null);
    }


    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    public void reply(@NotNull EmbedBuilder builder) {
        reply(builder, null);
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    public void reply(@NotNull EmbedDTO embedDTO) {
        reply(embedDTO, null);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link String} message to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull String message, @Nullable Consumer<Message> success) {
        replyCallback.sendMessage(message, success);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link Message} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull Message message, @Nullable Consumer<Message> success) {
        replyCallback.sendMessage(message, success);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link EmbedBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        replyCallback.sendMessage(builder, success);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link MessageBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull MessageBuilder builder, @Nullable Consumer<Message> success) {
        replyCallback.sendMessage(builder, success);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        replyCallback.sendMessage(embedDTO, success);
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
     * Get the {@link CommandDefinition} object which describes the command that is executed.
     *
     * @return the underlying {@link CommandDefinition} object
     */
    public CommandDefinition getCommandDefinition() {
        return commandDefinition;
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
}
