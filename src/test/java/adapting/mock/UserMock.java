package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class UserMock implements User {

    private final String name;
    private final long id;

    public UserMock(String name, long id) {
        this.name = name;
        this.id = id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getDiscriminator() {
        return null;
    }

    @Nullable
    @Override
    public String getAvatarId() {
        return null;
    }

    @NotNull
    @Override
    public String getDefaultAvatarId() {
        return null;
    }

    @Override
    public CacheRestAction<Profile> retrieveProfile() {
        return null;
    }

    @NotNull
    @Override
    public String getAsTag() {
        return null;
    }

    @Override
    public boolean hasPrivateChannel() {
        return false;
    }

    @Override
    public CacheRestAction<PrivateChannel> openPrivateChannel() {
        return null;
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds() {
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public boolean isSystem() {
        return false;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<UserFlag> getFlags() {
        return null;
    }

    @Override
    public int getFlagsRaw() {
        return 0;
    }

    @NotNull
    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public long getIdLong() {
        return id;
    }
}
