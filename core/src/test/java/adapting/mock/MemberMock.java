package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class MemberMock implements Member {

    private final String name;
    private final long id;

    public MemberMock(String name, long id) {
        this.name = name;
        this.id = id;
    }

    @NotNull
    @Override
    public User getUser() {
        return null;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissions() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissions(@NotNull GuildChannel channel) {
        return null;
    }


    @Override
    public EnumSet<Permission> getPermissionsExplicit() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissionsExplicit(@NotNull GuildChannel channel) {
        return null;
    }


    @Override
    public boolean hasPermission(@NotNull Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull Collection<Permission> collection) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull GuildChannel guildChannel, @NotNull Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull GuildChannel guildChannel, @NotNull Collection<Permission> collection) {
        return false;
    }

    @Override
    public boolean hasAccess(@NotNull GuildChannel channel) {
        return Member.super.hasAccess(channel);
    }

    @Override
    public boolean canSync(@NotNull IPermissionContainer iPermissionContainer, @NotNull IPermissionContainer iPermissionContainer1) {
        return false;
    }

    @Override
    public boolean canSync(@NotNull IPermissionContainer iPermissionContainer) {
        return false;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public OffsetDateTime getTimeJoined() {
        return null;
    }

    @Override
    public boolean hasTimeJoined() {
        return false;
    }

    @Nullable
    @Override
    public OffsetDateTime getTimeBoosted() {
        return null;
    }

    @Override
    public boolean isBoosting() {
        return false;
    }

    @Nullable
    @Override
    public OffsetDateTime getTimeOutEnd() {
        return null;
    }

    @Nullable
    @Override
    public GuildVoiceState getVoiceState() {
        return null;
    }

    @NotNull
    @Override
    public List<Activity> getActivities() {
        return null;
    }

    @NotNull
    @Override
    public OnlineStatus getOnlineStatus() {
        return null;
    }

    @NotNull
    @Override
    public OnlineStatus getOnlineStatus(@NotNull ClientType clientType) {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<ClientType> getActiveClients() {
        return null;
    }

    @Nullable
    @Override
    public String getNickname() {
        return name;
    }

    @NotNull
    @Override
    public String getEffectiveName() {
        return name;
    }

    @Nullable
    @Override
    public String getAvatarId() {
        return null;
    }

    @Nullable
    @Override
    public String getAvatarUrl() {
        return Member.super.getAvatarUrl();
    }

    @NotNull
    @Override
    public String getEffectiveAvatarUrl() {
        return Member.super.getEffectiveAvatarUrl();
    }

    @NotNull
    @Override
    public List<Role> getRoles() {
        return null;
    }

    @Override
    public @NotNull @Unmodifiable Set<Role> getUnsortedRoles() {
        return Set.of();
    }

    @Nullable
    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public int getColorRaw() {
        return 0;
    }

    @Override
    public int getFlagsRaw() {
        return 0;
    }

    @Override
    public boolean canInteract(@NotNull Member member) {
        return false;
    }

    @Override
    public boolean canInteract(@NotNull Role role) {
        return false;
    }

    @Override
    public boolean canInteract(@NotNull RichCustomEmoji richCustomEmoji) {
        return false;
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Nullable
    @Override
    public DefaultGuildChannelUnion getDefaultChannel() {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> kick() {
        return Member.super.kick();
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> mute(boolean mute) {
        return Member.super.mute(mute);
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> deafen(boolean deafen) {
        return Member.super.deafen(deafen);
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> modifyNickname(@Nullable String nickname) {
        return Member.super.modifyNickname(nickname);
    }

    @NotNull
    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        Member.super.formatTo(formatter, flags, width, precision);
    }

    @NotNull
    @Override
    public String getId() {
        return Member.super.getId();
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @NotNull
    @Override
    public OffsetDateTime getTimeCreated() {
        return Member.super.getTimeCreated();
    }

    @NotNull
    @Override
    public String getDefaultAvatarId() {
        return null;
    }

    @Override
    public boolean isDetached() {
        return false;
    }
}
