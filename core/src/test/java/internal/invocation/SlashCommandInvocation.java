package internal.invocation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SlashCommandInvocation {

    private final IEventManager eventManager;
    private final SlashCommandInteractionEvent event;
    private final InteractionHook hook;
    private final CompletableFuture<MessageCreateData> reply = new CompletableFuture<>();
    private User user;

    public SlashCommandInvocation(IEventManager eventManager, String command) {
        this.eventManager = eventManager;

        event = mock(SlashCommandInteractionEvent.class);
        when(event.getFullCommandName()).thenReturn(command);
        when(event.getOption(anyString())).thenCallRealMethod();
        when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

        user = mock(User.class);
        when(event.getUser()).then((_) -> user);

        hook = mock(InteractionHook.class);
        when(event.getHook()).thenReturn(hook);
        lenient().when(hook.sendMessage(any(MessageCreateData.class))).then(invocation -> {
            reply.complete(MessageCreateData.fromEditData(invocation.getArgument(0)));
            return mock(WebhookMessageEditAction.class);
        });
        lenient().when(hook.editOriginal(any(MessageEditData.class))).then(invocation -> {
            reply.complete(MessageCreateData.fromEditData(invocation.getArgument(0)));
            return mock(WebhookMessageEditAction.class);
        });
    }

    public SlashCommandInvocation options(Collection<OptionMapping> optionMapping) {
        when(event.getOptions()).thenReturn(List.copyOf(optionMapping));
        return this;
    }

    public SlashCommandInvocation channel(MessageChannelUnion channel) {
        when(event.getChannel()).thenReturn(channel);
        return this;
    }

    public SlashCommandInvocation guild(Guild guild) {
        when(event.getGuild()).thenReturn(guild);
        return this;
    }

    public SlashCommandInvocation member(Member member) {
        user = member.getUser();
        when(event.getMember()).thenReturn(member);
        return this;
    }

    public CompletableFuture<MessageCreateData> invoke() {
        eventManager.handle(event);
        return reply;
    }

}
