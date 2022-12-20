package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MessageMock implements Message {

    @Nullable
    @Override
    public MessageReference getMessageReference() {
        return null;
    }

    @NotNull
    @Override
    public Mentions getMentions() {
        return null;
    }

    @Override
    public boolean isEdited() {
        return false;
    }

    @Nullable
    @Override
    public OffsetDateTime getTimeEdited() {
        return null;
    }

    @NotNull
    @Override
    public User getAuthor() {
        return null;
    }

    @Nullable
    @Override
    public Member getMember() {
        return null;
    }

    @NotNull
    @Override
    public String getJumpUrl() {
        return null;
    }

    @NotNull
    @Override
    public String getContentDisplay() {
        return null;
    }

    @NotNull
    @Override
    public String getContentRaw() {
        return null;
    }

    @NotNull
    @Override
    public String getContentStripped() {
        return null;
    }

    @NotNull
    @Override
    public List<String> getInvites() {
        return null;
    }

    @Nullable
    @Override
    public String getNonce() {
        return null;
    }

    @Override
    public boolean isFromType(@NotNull ChannelType channelType) {
        return false;
    }

    @NotNull
    @Override
    public ChannelType getChannelType() {
        return null;
    }

    @Override
    public boolean isWebhookMessage() {
        return false;
    }

    @Override
    public long getApplicationIdLong() {
        return 0;
    }

    @Override
    public MessageChannelUnion getChannel() {
        return null;
    }

    @NotNull
    @Override
    public GuildMessageChannelUnion getGuildChannel() {
        return null;
    }

    @Nullable
    @Override
    public Category getCategory() {
        return null;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return null;
    }

    @NotNull
    @Override
    public List<Attachment> getAttachments() {
        return null;
    }

    @NotNull
    @Override
    public List<MessageEmbed> getEmbeds() {
        return null;
    }

    @NotNull
    @Override
    public List<LayoutComponent> getComponents() {
        return null;
    }

    @NotNull
    @Override
    public List<ActionRow> getActionRows() {
        return null;
    }

    @NotNull
    @Override
    public List<MessageReaction> getReactions() {
        return null;
    }

    @NotNull
    @Override
    public List<StickerItem> getStickers() {
        return null;
    }

    @Override
    public boolean isTTS() {
        return false;
    }

    @Nullable
    @Override
    public MessageActivity getActivity() {
        return null;
    }

    @NotNull
    @Override
    public MessageEditAction editMessage(@NotNull CharSequence charSequence) {
        return null;
    }

    @NotNull
    @Override
    public MessageEditAction editMessage(@NotNull MessageEditData messageEditData) {
        return null;
    }

    @NotNull
    @Override
    public MessageEditAction editMessageEmbeds(@NotNull Collection<? extends MessageEmbed> collection) {
        return null;
    }

    @NotNull
    @Override
    public MessageEditAction editMessageComponents(@NotNull Collection<? extends LayoutComponent> collection) {
        return null;
    }

    @NotNull
    @Override
    public MessageEditAction editMessageFormat(@NotNull String s, @NotNull Object... objects) {
        return null;
    }

    @NotNull
    @Override
    public MessageEditAction editMessageAttachments(@NotNull Collection<? extends AttachedFile> collection) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @NotNull
    @Override
    public RestAction<Void> pin() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> unpin() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> addReaction(@NotNull Emoji emoji) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactions() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactions(@NotNull Emoji emoji) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> removeReaction(@NotNull Emoji emoji) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> removeReaction(@NotNull Emoji emoji, @NotNull User user) {
        return null;
    }

    @NotNull
    @Override
    public ReactionPaginationAction retrieveReactionUsers(@NotNull Emoji emoji) {
        return null;
    }

    @Nullable
    @Override
    public MessageReaction getReaction(@NotNull Emoji emoji) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> suppressEmbeds(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Message> crosspost() {
        return null;
    }

    @Override
    public boolean isSuppressedEmbeds() {
        return false;
    }

    @NotNull
    @Override
    public EnumSet<MessageFlag> getFlags() {
        return null;
    }

    @Override
    public long getFlagsRaw() {
        return 0;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Nullable
    @Override
    public ThreadChannel getStartedThread() {
        return null;
    }

    @NotNull
    @Override
    public MessageType getType() {
        return null;
    }

    @Nullable
    @Override
    public Interaction getInteraction() {
        return null;
    }

    @Override
    public ThreadChannelAction createThreadChannel(String s) {
        return null;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {

    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
