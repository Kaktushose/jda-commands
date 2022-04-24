package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("ConstantConditions")
public class GuildMock implements Guild {

    public static final Member MEMBER = new MemberMock("member", 1);
    public static final Role ROLE = new RoleMock("role", 2);
    public static final TextChannel TEXT_CHANNEL = new TextChannelMock("channel", 3);
    public static final VoiceChannel VOICE_CHANNEL = new VoiceChannelMock("voice channel", 4);

    @NotNull
    @Override
    public RestAction<Member> retrieveMemberById(@NotNull String id) {
        if (id.equals(MEMBER.getId())) {
            return new RestActionMock<>(MEMBER);
        }
        throw ErrorResponseException.create(ErrorResponse.UNKNOWN_USER, new Response(new IllegalArgumentException(), new HashSet<>()));
    }

    @NotNull
    @Override
    public List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
        String username = MEMBER.getNickname();
        if (ignoreCase) {
            username = username.toUpperCase();
            name = name.toUpperCase();
        }
        if (name.equals(username)) {
            return new ArrayList<Member>() {{
                add(MEMBER);
            }};
        }
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public Role getRoleById(@NotNull String id) {
        if (id.equals(ROLE.getId())) {
            return ROLE;
        }
        return null;
    }

    @NotNull
    @Override
    public List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
        String username = ROLE.getName();
        if (ignoreCase) {
            username = username.toUpperCase();
            name = name.toUpperCase();
        }
        if (name.equals(username)) {
            return new ArrayList<Role>() {{
                add(ROLE);
            }};
        }
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public TextChannel getTextChannelById(@NotNull String id) {
        if (id.equals(TEXT_CHANNEL.getId())) {
            return TEXT_CHANNEL;
        }
        return null;
    }

    @NotNull
    @Override
    public List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
        String username = TEXT_CHANNEL.getName();
        if (ignoreCase) {
            username = username.toUpperCase();
            name = name.toUpperCase();
        }
        if (name.equals(username)) {
            return new ArrayList<TextChannel>() {{
                add(TEXT_CHANNEL);
            }};
        }
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public VoiceChannel getVoiceChannelById(@NotNull String id) {
        if (id.equals(VOICE_CHANNEL.getId())) {
            return VOICE_CHANNEL;
        }
        return null;
    }

    @NotNull
    @Override
    public List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
        String username = VOICE_CHANNEL.getName();
        if (ignoreCase) {
            username = username.toUpperCase();
            name = name.toUpperCase();
        }
        if (name.equals(username)) {
            return new ArrayList<VoiceChannel>() {{
                add(VOICE_CHANNEL);
            }};
        }
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public RestAction<Member> retrieveMemberById(long l, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public Task<List<Member>> retrieveMembersByIds(boolean b, @NotNull long... longs) {
        return null;
    }

    @NotNull
    @Override
    public Task<List<Member>> retrieveMembersByPrefix(@NotNull String s, int i) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> moveVoiceMember(@NotNull Member member, @Nullable VoiceChannel voiceChannel) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> modifyNickname(@NotNull Member member, @Nullable String s) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Integer> prune(int i, boolean b, @NotNull Role... roles) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> kick(@NotNull Member member, @Nullable String s) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> kick(@NotNull String s, @Nullable String s1) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> ban(@NotNull User user, int i, @Nullable String s) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> ban(@NotNull String s, int i, @Nullable String s1) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> unban(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> deafen(@NotNull Member member, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> mute(@NotNull Member member, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> addRoleToMember(@NotNull Member member, @NotNull Role role) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> removeRoleFromMember(@NotNull Member member, @NotNull Role role) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @Nullable Collection<Role> collection, @Nullable Collection<Role> collection1) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> collection) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> transferOwnership(@NotNull Member member) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<TextChannel> createTextChannel(@NotNull String s, @Nullable Category category) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String s, @Nullable Category category) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<StageChannel> createStageChannel(@NotNull String s, @Nullable Category category) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<Category> createCategory(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RoleAction createRole() {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Emote> createEmote(@NotNull String s, @NotNull Icon icon, @NotNull Role... roles) {
        return null;
    }

    @NotNull
    @Override
    public ChannelOrderAction modifyCategoryPositions() {
        return null;
    }

    @NotNull
    @Override
    public ChannelOrderAction modifyTextChannelPositions() {
        return null;
    }

    @NotNull
    @Override
    public ChannelOrderAction modifyVoiceChannelPositions() {
        return null;
    }

    @NotNull
    @Override
    public CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
        return null;
    }

    @NotNull
    @Override
    public CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
        return null;
    }

    @NotNull
    @Override
    public RoleOrderAction modifyRolePositions(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Command>> retrieveCommands() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Command> retrieveCommandById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public CommandCreateAction upsertCommand(@NotNull CommandData commandData) {
        return null;
    }

    @NotNull
    @Override
    public CommandListUpdateAction updateCommands() {
        return null;
    }

    @NotNull
    @Override
    public CommandEditAction editCommandById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> deleteCommandById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Map<String, List<CommandPrivilege>>> retrieveCommandPrivileges() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String s, @NotNull Collection<? extends CommandPrivilege> collection) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Map<String, List<CommandPrivilege>>> updateCommandPrivileges(@NotNull Map<String, Collection<? extends CommandPrivilege>> map) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<EnumSet<Region>> retrieveRegions(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public MemberAction addMember(@NotNull String s, @NotNull String s1) {
        return null;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public void pruneMemberCache() {

    }

    @Override
    public boolean unloadMember(long l) {
        return false;
    }

    @Override
    public int getMemberCount() {
        return 0;
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @Nullable
    @Override
    public String getIconId() {
        return null;
    }

    @NotNull
    @Override
    public Set<String> getFeatures() {
        return null;
    }

    @Nullable
    @Override
    public String getSplashId() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<String> retrieveVanityUrl() {
        return null;
    }

    @Nullable
    @Override
    public String getVanityCode() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<VanityInvite> retrieveVanityInvite() {
        return null;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @NotNull
    @Override
    public Locale getLocale() {
        return null;
    }

    @Nullable
    @Override
    public String getBannerId() {
        return null;
    }

    @NotNull
    @Override
    public BoostTier getBoostTier() {
        return null;
    }

    @Override
    public int getBoostCount() {
        return 0;
    }

    @NotNull
    @Override
    public List<Member> getBoosters() {
        return null;
    }

    @Override
    public int getMaxMembers() {
        return 0;
    }

    @Override
    public int getMaxPresences() {
        return 0;
    }

    @NotNull
    @Override
    public RestAction<MetaData> retrieveMetaData() {
        return null;
    }

    @Nullable
    @Override
    public VoiceChannel getAfkChannel() {
        return null;
    }

    @Nullable
    @Override
    public TextChannel getSystemChannel() {
        return null;
    }

    @Nullable
    @Override
    public TextChannel getRulesChannel() {
        return null;
    }

    @Nullable
    @Override
    public TextChannel getCommunityUpdatesChannel() {
        return null;
    }

    @Nullable
    @Override
    public Member getOwner() {
        return null;
    }

    @Override
    public long getOwnerIdLong() {
        return 0;
    }

    @NotNull
    @Override
    public Timeout getAfkTimeout() {
        return null;
    }

    @NotNull
    @Override
    public String getRegionRaw() {
        return null;
    }

    @Override
    public boolean isMember(@NotNull User user) {
        return false;
    }

    @NotNull
    @Override
    public Member getSelfMember() {
        return null;
    }

    @NotNull
    @Override
    public NSFWLevel getNSFWLevel() {
        return null;
    }

    @Nullable
    @Override
    public Member getMember(@NotNull User user) {
        return null;
    }

    @NotNull
    @Override
    public SortedSnowflakeCacheView<Category> getCategoryCache() {
        return null;
    }

    @NotNull
    @Override
    public SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SortedSnowflakeCacheView<Role> getRoleCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<ListedEmote>> retrieveEmotes() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<ListedEmote> retrieveEmoteById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Ban>> retrieveBanList() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Ban> retrieveBanById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Integer> retrievePrunableMemberCount(int i) {
        return null;
    }

    @NotNull
    @Override
    public Role getPublicRole() {
        return null;
    }

    @Nullable
    @Override
    public TextChannel getDefaultChannel() {
        return null;
    }

    @NotNull
    @Override
    public GuildManager getManager() {
        return null;
    }

    @NotNull
    @Override
    public MemberCacheView getMemberCache() {
        return null;
    }

    @NotNull
    @Override
    public AuditLogPaginationAction retrieveAuditLogs() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> leave() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> delete() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> delete(@Nullable String s) {
        return null;
    }

    @NotNull
    @Override
    public AudioManager getAudioManager() {
        return null;
    }

    @NotNull
    @Override
    public Task<Void> requestToSpeak() {
        return null;
    }

    @NotNull
    @Override
    public Task<Void> cancelRequestToSpeak() {
        return null;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Invite>> retrieveInvites() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Template>> retrieveTemplates() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Template> createTemplate(@NotNull String s, @Nullable String s1) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Webhook>> retrieveWebhooks() {
        return null;
    }

    @NotNull
    @Override
    public List<GuildVoiceState> getVoiceStates() {
        return null;
    }

    @NotNull
    @Override
    public VerificationLevel getVerificationLevel() {
        return null;
    }

    @NotNull
    @Override
    public NotificationLevel getDefaultNotificationLevel() {
        return null;
    }

    @NotNull
    @Override
    public MFALevel getRequiredMFALevel() {
        return null;
    }

    @NotNull
    @Override
    public ExplicitContentLevel getExplicitContentLevel() {
        return null;
    }

    @Override
    public boolean checkVerification() {
        return false;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @NotNull
    @Override
    public CompletableFuture<Void> retrieveMembers() {
        return null;
    }

    @NotNull
    @Override
    public Task<Void> loadMembers(@NotNull Consumer<Member> consumer) {
        return null;
    }

    @NotNull
    @Override
    public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public List<GuildChannel> getChannels(boolean b) {
        return null;
    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
