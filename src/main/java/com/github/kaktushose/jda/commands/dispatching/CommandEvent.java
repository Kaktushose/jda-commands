package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

/**
 * This class is a subclass of the {@code GuildMessageReceivedEvent} from JDA.
 * It provides some additional features for sending messages and also grants
 * access to the {@link CommandDefinition} object which describes the command that is executed.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 1.0.0
 */
public class CommandEvent extends MessageReceivedEvent {

    private final CommandDefinition commandDefinition;
    private final CommandContext context;

    /**
     * Constructs a CommandEvent.
     *
     * @param api            the {@code JDA}, needed for the {@code GuildMessageReceivedEvent}
     * @param responseNumber the responseNumber, needed for the {@code GuildMessageReceivedEvent}
     * @param message        the {@code Message}, needed for the {@code GuildMessageReceivedEvent}
     * @param command        the underlying {@link CommandDefinition} object
     * @param context        the {@link CommandContext}
     */
    public CommandEvent(@NotNull JDA api,
                        long responseNumber,
                        @NotNull Message message,
                        @NotNull CommandDefinition command,
                        @NotNull CommandContext context
    ) {
        super(api, responseNumber, message);
        this.commandDefinition = command;
        this.context = context;
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param message the message to send
     */
    public void reply(@NotNull String message) {
        getChannel().sendMessage(message).queue();
    }

    /**
     * Sends a message to the TextChannel where the command was called and attaches a file to it.
     *
     * @param file    The file to send.
     * @param message The message to send
     */
    public void reply(@NotNull File file, @NotNull String message) {
        getChannel().sendMessage(message).addFile(file).queue();
    }

    /**
     * Sends a file to the TextChannel where the command was called.
     *
     * @param file The file to send.
     */
    public void reply(@NotNull File file) {
        getChannel().sendFile(file).queue();
    }

    /**
     * Sends a formatted message using the specified format string and arguments to the TextChannel where the command was called, as well as attaching a file to the message.
     *
     * @param file   The file to send.
     * @param format The message to send
     * @param args   Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *               format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *               zero.
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
     *                                          is incompatible with the given arguments, insufficient arguments given
     *                                          the format string, or other illegal conditions.
     */
    public void reply(@NotNull File file, @NotNull String format, @NotNull Object... args) {
        String message = String.format(format, args);
        getChannel().sendFile(file, message).queue();
    }

    /**
     * Sends a message to the TextChannel where the command was called and attaches a File to it. This method also allows to access the JDA RestAction consumer.
     *
     * @param file    The file to send.
     * @param message The message to send
     * @param success The JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull File file, @NotNull String message, @Nullable Consumer<Message> success) {
        getChannel().sendMessage(message).addFile(file).queue(success);
    }

    /**
     * Sends a file to the TextChannel where the command was called. This method also allows to access the JDA RestAction consumer.
     * @param file    The file to send.
     * @param success The JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull File file, @Nullable Consumer<Message> success) {
        getChannel().sendFile(file).queue(success);
    }

    /**
     * Sends a message to the TextChannel where the command was called and attaches a file to it.
     *
     * @param messageBuilder The {@code MessageBuilder} to send
     */
    public void reply(@NotNull File file, @NotNull MessageBuilder messageBuilder) {
        getChannel().sendMessage(messageBuilder.build()).addFile(file).queue();
    }

    /**
     * Sends a message to the TextChannel where the command was called and attaches a file to it. This method also allows to access the JDA RestAction consumer.
     *
     * @param file           The file to send.
     * @param messageBuilder The {@code MessageBuilder} to send
     * @param success        The JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull File file, @NotNull MessageBuilder messageBuilder, @Nullable Consumer<Message> success) {
        getChannel().sendMessage(messageBuilder.build()).addFile(file).queue(success);
    }

    /**
     * Sends a message embed to the TextChannel where the command was called and attach a file to it.
     * @param file         The file to send.
     * @param embedBuilder The {@code EmbedBuilder} to send
     */
    public void reply(@NotNull File file, @NotNull EmbedBuilder embedBuilder) {
        getChannel().sendMessageEmbeds(embedBuilder.build()).addFile(file).queue();
    }

    /**
     * Sends a message embed to the TextChannel where the command was called and attach a file to it. This method also allows to access the JDA RestAction consumer.
     * @param file         The file to send.
     * @param embedBuilder The {@code EmbedBuilder} to send
     * @param success      The JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull File file, @NotNull EmbedBuilder embedBuilder, @Nullable Consumer<Message> success) {
        getChannel().sendMessageEmbeds(embedBuilder.build()).addFile(file).queue(success);
    }

    /**
     * Sends a message embed DTO to the TextChannel where the command was called and attach a file to it.
     *
     * @param file     The file to send.
     * @param embedDTO The {@link EmbedDTO} to send
     */
    public void reply(@NotNull File file, @NotNull EmbedDTO embedDTO) {
        getChannel().sendMessageEmbeds(embedDTO.toEmbedBuilder().build()).addFile(file).queue();
    }

    /**
     * Sends a message embed DTO to the TextChannel where the command was called and attach a file to it. This method also allows to access the JDA RestAction consumer.
     *
     * @param file     The file to send.
     * @param embedDTO The {@link EmbedDTO} to send
     * @param success  The JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull File file, @NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        getChannel().sendMessageEmbeds(embedDTO.toEmbedBuilder().build()).addFile(file).queue(success);
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
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the message to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull String message, @Nullable Consumer<Message> success) {
        getChannel().sendMessage(message).queue(success);
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param message the {@code Message} to send
     */
    public void reply(@NotNull Message message) {
        getChannel().sendMessage(message).queue();
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@code Message} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull Message message, @Nullable Consumer<Message> success) {
        getChannel().sendMessage(message).queue(success);
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param messageBuilder the {@code MessageBuilder} to send
     */
    public void reply(@NotNull MessageBuilder messageBuilder) {
        getChannel().sendMessage(messageBuilder.build()).queue();
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param messageBuilder the {@code MessageBuilder} to send
     * @param success        the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull MessageBuilder messageBuilder, @Nullable Consumer<Message> success) {
        getChannel().sendMessage(messageBuilder.build()).queue(success);
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param embedBuilder the {@code EmbedBuilder} to send
     */
    public void reply(@NotNull EmbedBuilder embedBuilder) {
        getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedBuilder the {@code EmbedBuilder} to send
     * @param success      the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull EmbedBuilder embedBuilder, @Nullable Consumer<Message> success) {
        getChannel().sendMessageEmbeds(embedBuilder.build()).queue(success);
    }

    /**
     * Sends a message to the TextChannel where the command was called.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    public void reply(@NotNull EmbedDTO embedDTO) {
        getChannel().sendMessageEmbeds(embedDTO.toEmbedBuilder().build()).queue();
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
        getChannel().sendMessageEmbeds(embedDTO.toEmbedBuilder().build()).queue(success);
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

}
