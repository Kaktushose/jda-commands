package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class VoiceChannelMock implements VoiceChannel {

    private final String name;
    private final long id;

    public VoiceChannelMock(String name, long id) {
        this.name = name;
        this.id = id;
    }


    @Override
    public int getUserLimit() {
        return 0;
    }

    @Override
    public int getBitrate() {
        return 0;
    }

    @NotNull
    @Override
    public Region getRegion() {
        return null;
    }

    @Nullable
    @Override
    public String getRegionRaw() {
        return null;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return null;
    }

    @Nullable
    @Override
    public Category getParent() {
        return null;
    }

    @NotNull
    @Override
    public List<Member> getMembers() {
        return null;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public int getPositionRaw() {
        return 0;
    }

    @Nullable
    @Override
    public PermissionOverride getPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @NotNull
    @Override
    public List<PermissionOverride> getPermissionOverrides() {
        return null;
    }

    @NotNull
    @Override
    public List<PermissionOverride> getMemberPermissionOverrides() {
        return null;
    }

    @NotNull
    @Override
    public List<PermissionOverride> getRolePermissionOverrides() {
        return null;
    }

    @Override
    public boolean isSynced() {
        return false;
    }

    @NotNull
    @Override
    public ChannelAction<VoiceChannel> createCopy(@NotNull Guild guild) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<VoiceChannel> createCopy() {
        return null;
    }

    @NotNull
    @Override
    public ChannelManager getManager() {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @NotNull
    @Override
    public PermissionOverrideAction createPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @NotNull
    @Override
    public PermissionOverrideAction putPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @NotNull
    @Override
    public InviteAction createInvite() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Invite>> retrieveInvites() {
        return null;
    }

    @Override
    public int compareTo(@NotNull GuildChannel o) {
        return 0;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public ChannelType getType() {
        return null;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
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
