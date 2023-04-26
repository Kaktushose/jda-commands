package adapting.mock;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlashCommandInteractionEventMock extends SlashCommandInteractionEvent {


    public SlashCommandInteractionEventMock() {
        super(new JDAMock(), 0, new SlashCommandInteractionMock());
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return new GuildMock();
    }

    @NotNull
    @Override
    public User getUser() {
        return new UserMock("user", 0);
    }

    @Nullable
    @Override
    public Member getMember() {
        return GuildMock.MEMBER;
    }

    @NotNull
    @Override
    public ChannelType getChannelType() {
        return ChannelType.TEXT;
    }

    @NotNull
    @Override
    public String getFullCommandName() {
        return "command group sub";
    }

    @NotNull
    @Override
    public MessageChannel getMessageChannel() {
        return new TextChannelMock("channel", 0);
    }
}
