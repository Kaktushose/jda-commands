package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class TextChannelMock implements TextChannel {

    private final String name;
    private final long id;

    public TextChannelMock(String name, long id) {
        this.name = name;
        this.id = id;
    }

    @Nullable
    @Override
    public String getTopic() {
        return null;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    @Override
    public int getSlowmode() {
        return 0;
    }

    @NotNull
    @Override
    public Guild getGuild() {
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

    @NotNull
    @Override
    public PermissionOverrideAction upsertPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @Override
    public boolean isSynced() {
        return false;
    }

    @NotNull
    @Override
    public ChannelAction<TextChannel> createCopy(@NotNull Guild guild) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<TextChannel> createCopy() {
        return null;
    }

    @NotNull
    @Override
    public TextChannelManager getManager() {
        return null;
    }

    @Override
    public long getParentCategoryIdLong() {
        return 0;
    }


    @NotNull
    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @Override
    public IPermissionContainer getPermissionContainer() {
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

    @NotNull
    @Override
    public RestAction<List<Webhook>> retrieveWebhooks() {
        return null;
    }

    @NotNull
    @Override
    public WebhookAction createWebhook(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> deleteMessages(@NotNull Collection<Message> collection) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> deleteMessagesByIds(@NotNull Collection<String> collection) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> deleteWebhookById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(@NotNull String s, @NotNull String s1) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(@NotNull String s, @NotNull Emote emote) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(@NotNull String s, @NotNull String s1, @NotNull User user) {
        return null;
    }

    @Override
    public boolean canTalk() {
        return false;
    }

    @Override
    public boolean canTalk(@NotNull Member member) {
        return false;
    }

    @Override
    public int compareTo(@NotNull GuildChannel o) {
        return 0;
    }

    @Override
    public long getLatestMessageIdLong() {
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
        return ChannelType.TEXT;
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

    @NotNull
    @Override
    public ThreadChannelAction createThreadChannel(String s, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public ThreadChannelAction createThreadChannel(String s, long l) {
        return null;
    }

    @NotNull
    @Override
    public ThreadChannelPaginationAction retrieveArchivedPublicThreadChannels() {
        return null;
    }

    @NotNull
    @Override
    public ThreadChannelPaginationAction retrieveArchivedPrivateThreadChannels() {
        return null;
    }

    @NotNull
    @Override
    public ThreadChannelPaginationAction retrieveArchivedPrivateJoinedThreadChannels() {
        return null;
    }
}
