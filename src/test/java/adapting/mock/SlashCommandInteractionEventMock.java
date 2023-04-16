package adapting.mock;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class MessageReceivedEventMock extends MessageReceivedEvent {

    private final boolean isGuildEvent;

    public MessageReceivedEventMock(boolean isGuildEvent) {
        super(new JDAMock(), 0, new MessageMock());
        this.isGuildEvent = isGuildEvent;
    }

    public boolean isFromType(@NotNull ChannelType type) {
        return isGuildEvent;
    }

    public Guild getGuild() {
        return new GuildMock();
    }
}
