package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.PremiumRequiredCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"DataFlowIssue", "NullableProblems"})
public class SlashCommandInteractionMock implements SlashCommandInteraction {
    @Override
    public int getTypeRaw() {
        return 0;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return null;
    }

    @Override
    public User getUser() {
        return null;
    }

    @Nullable
    @Override
    public Member getMember() {
        return null;
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @Override
    public MessageChannelUnion getChannel() {
        return null;
    }

    @Override
    public long getChannelIdLong() {
        return 0;
    }

    @Override
    public DiscordLocale getUserLocale() {
        return null;
    }

    @NotNull
    @Override
    public List<Entitlement> getEntitlements() {
        return null;
    }

    @Override
    public @NotNull InteractionContextType getContext() {
        return null;
    }

    @Override
    public @NotNull IntegrationOwners getIntegrationOwners() {
        return null;
    }

    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public ModalCallbackAction replyModal(@NotNull Modal modal) {
        return null;
    }

    @Override
    public ReplyCallbackAction deferReply() {
        return null;
    }

    @Override
    public InteractionHook getHook() {
        return null;
    }

    @Override
    public Command.Type getCommandType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Nullable
    @Override
    public String getSubcommandName() {
        return null;
    }

    @Nullable
    @Override
    public String getSubcommandGroup() {
        return null;
    }

    @Override
    public long getCommandIdLong() {
        return 0;
    }

    @Override
    public boolean isGuildCommand() {
        return false;
    }

    @Override
    public List<OptionMapping> getOptions() {
        return null;
    }

    @Override
    public long getIdLong() {
        return 0;
    }

    @NotNull
    @Override
    public PremiumRequiredCallbackAction replyWithPremiumRequired() {
        return null;
    }
}
