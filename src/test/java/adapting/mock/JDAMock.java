package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.sticker.StickerUnion;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class JDAMock implements JDA {

    public static final User USER = new UserMock("user", 0);

    @NotNull
    @Override
    public CacheRestAction<User> retrieveUserById(@NotNull String id) {
        if (id.equals(USER.getId())) {
            return new CacheRestActionMock<>(USER);
        }
        throw ErrorResponseException.create(ErrorResponse.UNKNOWN_USER, new Response(new IllegalArgumentException(), new HashSet<>()));
    }

    @NotNull
    @Override
    public CacheRestAction<User> retrieveUserById(long id) {
        return null;
    }

    @NotNull
    @Override
    public List<User> getUsersByName(@NotNull String name, boolean ignoreCase) {
        String username = USER.getName();
        if (ignoreCase) {
            username = username.toUpperCase();
            name = name.toUpperCase();
        }
        if (name.equals(username)) {
            return new ArrayList<User>() {{
                add(USER);
            }};
        }
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public Status getStatus() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<GatewayIntent> getGatewayIntents() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<CacheFlag> getCacheFlags() {
        return null;
    }

    @Override
    public boolean unloadUser(long l) {
        return false;
    }

    @Override
    public long getGatewayPing() {
        return 0;
    }

    @NotNull
    @Override
    public JDA awaitStatus(@NotNull JDA.Status status, @NotNull Status... statuses) throws InterruptedException {
        return null;
    }

    @Override
    public boolean awaitShutdown(long duration, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public int cancelRequests() {
        return 0;
    }

    @NotNull
    @Override
    public ScheduledExecutorService getRateLimitPool() {
        return null;
    }

    @NotNull
    @Override
    public ScheduledExecutorService getGatewayPool() {
        return null;
    }

    @NotNull
    @Override
    public ExecutorService getCallbackPool() {
        return null;
    }

    @NotNull
    @Override
    public OkHttpClient getHttpClient() {
        return null;
    }

    @NotNull
    @Override
    public DirectAudioController getDirectAudioController() {
        return null;
    }

    @Override
    public void addEventListener(@NotNull Object... objects) {

    }

    @Override
    public void removeEventListener(@NotNull Object... objects) {

    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Command>> retrieveCommands() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
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
    public RestAction<List<RoleConnectionMetadata>> retrieveRoleConnectionMetadata() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<RoleConnectionMetadata>> updateRoleConnectionMetadata(@NotNull Collection<? extends RoleConnectionMetadata> records) {
        return null;
    }

    @NotNull
    @Override
    public GuildAction createGuild(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> createGuildFromTemplate(@NotNull String s, @NotNull String s1, @Nullable Icon icon) {
        return null;
    }

    @NotNull
    @Override
    public CacheView<AudioManager> getAudioManagerCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<User> getUserCache() {
        return null;
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds(@NotNull User... users) {
        return null;
    }

    @NotNull
    @Override
    public List<Guild> getMutualGuilds(@NotNull Collection<User> collection) {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Guild> getGuildCache() {
        return null;
    }

    @NotNull
    @Override
    public Set<String> getUnavailableGuilds() {
        return null;
    }

    @Override
    public boolean isUnavailable(long l) {
        return false;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Role> getRoleCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<StageChannel> getStageChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<Category> getCategoryCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<NewsChannel> getNewsChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<ForumChannel> getForumChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
        return null;
    }

    @NotNull
    @Override
    public CacheRestAction<PrivateChannel> openPrivateChannelById(long userId) {
        return null;
    }

    @NotNull
    @Override
    public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<StickerUnion> retrieveSticker(@NotNull StickerSnowflake stickerSnowflake) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<StickerPack>> retrieveNitroStickerPacks() {
        return null;
    }

    @NotNull
    @Override
    public IEventManager getEventManager() {
        return null;
    }

    @Override
    public void setEventManager(@Nullable IEventManager iEventManager) {

    }

    @NotNull
    @Override
    public SelfUser getSelfUser() {
        return null;
    }

    @NotNull
    @Override
    public Presence getPresence() {
        return null;
    }

    @NotNull
    @Override
    public ShardInfo getShardInfo() {
        return null;
    }

    @NotNull
    @Override
    public String getToken() {
        return null;
    }

    @Override
    public long getResponseTotal() {
        return 0;
    }

    @Override
    public int getMaxReconnectDelay() {
        return 0;
    }

    @Override
    public void setRequestTimeoutRetry(boolean b) {

    }

    @Override
    public boolean isAutoReconnect() {
        return false;
    }

    @Override
    public void setAutoReconnect(boolean b) {

    }

    @Override
    public boolean isBulkDeleteSplittingEnabled() {
        return false;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdownNow() {

    }

    @NotNull
    @Override
    public RestAction<ApplicationInfo> retrieveApplicationInfo() {
        return null;
    }

    @NotNull
    @Override
    public JDA setRequiredScopes(@NotNull Collection<String> collection) {
        return null;
    }

    @NotNull
    @Override
    public String getInviteUrl(@Nullable Permission... permissions) {
        return null;
    }

    @NotNull
    @Override
    public String getInviteUrl(@Nullable Collection<Permission> collection) {
        return null;
    }

    @Nullable
    @Override
    public ShardManager getShardManager() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Webhook> retrieveWebhookById(@NotNull String s) {
        return null;
    }
}
