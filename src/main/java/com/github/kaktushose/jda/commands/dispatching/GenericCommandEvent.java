package com.github.kaktushose.jda.commands.dispatching;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Bridge between {@link MessageReceivedEvent} and {@link SlashCommandInteractionEvent}. This class contains all methods
 * both events share and extends JDAs {@link Event}. In addition, this class provides the bare minimum needed for
 * {@link CommandContext} to function.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class GenericCommandEvent extends Event {

    private final Guild guild;
    private final User user;
    private final Member member;
    private final MessageChannel channel;
    private final ChannelType channelType;
    private final Message message;

    protected GenericCommandEvent(JDA api, long responseNumber, Guild guild, User user, Member member,
                                  MessageChannel channel, ChannelType channelType, Message message) {
        super(api, responseNumber);
        this.guild = guild;
        this.user = user;
        this.member = member;
        this.channel = channel;
        this.channelType = channelType;
        this.message = message;
    }

    protected GenericCommandEvent(GenericCommandEvent event) {
        this(event.getJDA(), event.getResponseNumber(), event.getGuild(), event.getUser(), event.getMember(),
                event.getChannel(), event.getChannelType(), event.getMessage());
    }

    /**
     * Constructs a new {@link GenericCommandEvent} from a {@link MessageReceivedEvent}.
     *
     * @param event the {@link MessageReceivedEvent} to construct from
     * @return a {@link GenericCommandEvent}
     */
    @NotNull
    public static GenericCommandEvent fromEvent(@NotNull MessageReceivedEvent event) {
        return new GenericCommandEvent(event.getJDA(), event.getResponseNumber(), event.getGuild(),
                event.getAuthor(), event.getMember(), event.getChannel(), event.getChannelType(), event.getMessage());
    }

    /**
     * Constructs a new {@link GenericCommandEvent} from a {@link SlashCommandInteractionEvent}.
     *
     * @param event the {@link SlashCommandInteractionEvent} to construct from
     * @return a {@link GenericCommandEvent}
     */
    @NotNull
    public static GenericCommandEvent fromEvent(@NotNull SlashCommandInteractionEvent event) {
        return new GenericCommandEvent(event.getJDA(), event.getResponseNumber(), event.getGuild(),
                event.getUser(), event.getMember(), event.getChannel(), event.getChannelType(), null);
    }

    /**
     * The {@link Guild} the Message was received in.
     * <br>If this Message was not received in a {@link TextChannel TextChannel},
     * this will throw an {@link java.lang.IllegalStateException}.
     *
     * @return The Guild the Message was received in
     * @throws IllegalStateException If this was not sent in a {@link TextChannel}
     * @see #isFromGuild()
     * @see #isFromType(ChannelType)
     * @see #getChannelType()
     */
    @NotNull
    public Guild getGuild() {
        if (!isFromGuild()) {
            throw new IllegalStateException("This message event did not happen in a guild");
        }
        return guild;
    }

    /**
     * Whether this message was sent in a {@link Guild Guild}.
     * <br>If this is {@code false} then {@link #getGuild()} will throw an {@link IllegalStateException}.
     *
     * @return {@code true}, if {@link #getChannelType()}.{@link ChannelType#isGuild() isGuild()} is true
     */
    public boolean isFromGuild() {
        return channelType.isGuild();
    }

    /**
     * The Author of the Message received as {@link User} object.
     * <br>This will be never-null but might be a fake user if Message was sent via Webhook (Guild only).
     * See {@link Webhook#getDefaultUser()}.
     *
     * @return The Author of the Message
     * @see Message#isWebhookMessage()
     */
    @NotNull
    public User getUser() {
        return user;
    }

    /**
     * The Author of the Message received as {@link User} object.
     * <br>This will be never-null but might be a fake user if Message was sent via Webhook (Guild only).
     * See {@link Webhook#getDefaultUser()}.
     *
     * @return The Author of the Message
     * @see Message#isWebhookMessage()
     */
    @NotNull
    public User getAuthor() {
        return user;
    }

    /**
     * The Author of the Message received as {@link Member} object.
     * <br>This will be {@code null} in case of Message being received in
     * a {@link PrivateChannel PrivateChannel}
     * or {@link Message#isWebhookMessage() isWebhookMessage()} returning {@code true}.
     *
     * @return The Author of the Message as null-able Member object
     * @see Message#isWebhookMessage()
     */
    @Nullable
    public Member getMember() {
        return member;
    }

    /**
     * The received {@link Message} object.
     * <br>This will be {@code null} if this instance was not created from a {@link MessageReceivedEvent}.
     *
     * @return The received {@link Message} object
     */
    @Nullable
    public Message getMessage() {
        return message;
    }

    /**
     * The {@link MessageChannel} for this Message.
     *
     * @return The MessageChannel
     */
    @NotNull
    public MessageChannel getChannel() {
        return channel;
    }

    /**
     * The {@link NewsChannel} the Message was received in.
     * <br>If this Message was not received in a {@link NewsChannel}, this will throw an {@link IllegalStateException}.
     *
     * @return The NewsChannel the Message was received in
     * @throws IllegalStateException If this was not sent in a {@link NewsChannel}
     * @see #isFromGuild()
     * @see #isFromType(ChannelType)
     * @see #getChannelType()
     */
    public NewsChannel getNewsChannel() {
        if (channel instanceof NewsChannel) {
            return (NewsChannel) channel;
        } else {
            throw new IllegalStateException("Cannot convert channel of type " + channelType + " to NewsChannel");
        }
    }

    /**
     * The {@link TextChannel} the Message was received in.
     * <br>If this Message was not received in a {@link TextChannel}, this will throw an {@link IllegalStateException}.
     *
     * @return The TextChannel the Message was received in
     * @throws IllegalStateException If this was not sent in a {@link TextChannel}
     * @see #isFromGuild()
     * @see #isFromType(ChannelType)
     * @see #getChannelType()
     */
    @NotNull
    public TextChannel getTextChannel() {
        if (channel instanceof NewsChannel) {
            return (TextChannel) channel;
        } else {
            throw new IllegalStateException("Cannot convert channel of type " + channelType + " to TextChannel");
        }
    }

    /**
     * The {@link ThreadChannel} the Message was received in.
     * <br>If this Message was not received in a {@link ThreadChannel}, this will throw an {@link IllegalStateException}.
     *
     * @return The ThreadChannel the Message was received in
     * @throws IllegalStateException If this was not sent in a {@link ThreadChannel}
     * @see #isFromGuild()
     * @see #isFromType(ChannelType)
     * @see #getChannelType()
     */
    public ThreadChannel getThreadChannel() {
        if (channel instanceof NewsChannel) {
            return (ThreadChannel) channel;
        } else {
            throw new IllegalStateException("Cannot convert channel of type " + channelType + " to ThreadChannel");
        }
    }

    /**
     * The {@link GuildChannel} the Message was received in.
     * <br>If this Message was not received in a {@link GuildChannel}, this will throw an {@link IllegalStateException}.
     *
     * @return The GuildChannel the Message was received in
     * @throws IllegalStateException If this was not sent in a {@link GuildChannel}
     * @see #isFromGuild()
     * @see #isFromType(ChannelType)
     * @see #getChannelType()
     */
    public GuildChannel getGuildChannel() {
        if (channel instanceof NewsChannel) {
            return (GuildChannel) channel;
        } else {
            throw new IllegalStateException("Cannot convert channel of type " + channelType + " to GuildChannel");
        }
    }

    /**
     * The {@link PrivateChannel} the Message was received in.
     * <br>If this Message was not received in a {@link PrivateChannel}, this will throw an {@link IllegalStateException}.
     *
     * @return The PrivateChannel the Message was received in
     * @throws IllegalStateException If this was not sent in a {@link PrivateChannel}
     * @see #isFromGuild()
     * @see #isFromType(ChannelType)
     * @see #getChannelType()
     */
    public PrivateChannel getPrivateChannel() {
        if (channel instanceof NewsChannel) {
            return (PrivateChannel) channel;
        } else {
            throw new IllegalStateException("Cannot convert channel of type " + channelType + " to PrivateChannel");
        }
    }

    /**
     * The {@link ChannelType} for this message.
     *
     * @return The ChannelType
     */
    @NotNull
    public ChannelType getChannelType() {
        return channelType;
    }

    /**
     * Indicates whether the message is from the specified {@link ChannelType}.
     *
     * @param type The ChannelType
     * @return {@code true}, if the message is from the specified channel type
     */
    public boolean isFromType(ChannelType type) {
        return type == channelType;
    }
}
