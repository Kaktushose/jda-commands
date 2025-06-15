package internal.invocation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

public abstract sealed class Invocation<T extends IReplyCallback> permits SlashCommandInvocation, ButtonInvocation {

    protected final IEventManager eventManager;
    protected final T event;
    protected final CompletableFuture<MessageCreateData> reply = new CompletableFuture<>();
    private User user;

    public Invocation(IEventManager eventManager, Class<T> eventClass) {
        this.eventManager = eventManager;

        event = mock(eventClass);
        lenient().when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

        user = mock(User.class);
        when(event.getUser()).then((_) -> user);

        InteractionHook hook = mock(InteractionHook.class);
        lenient().when(event.getHook()).thenReturn(hook);
        lenient().when(hook.sendMessage(any(MessageCreateData.class))).then(invocation -> {
            reply.complete(MessageCreateData.fromEditData(invocation.getArgument(0)));
            return mock(WebhookMessageEditAction.class);
        });
        lenient().when(hook.editOriginal(any(MessageEditData.class))).then(invocation -> {
            reply.complete(MessageCreateData.fromEditData(invocation.getArgument(0)));
            return mock(WebhookMessageEditAction.class);
        });

        lenient().when(event.getUserLocale()).thenReturn(DiscordLocale.ENGLISH_US);
        lenient().when(event.getGuildLocale()).thenReturn(DiscordLocale.ENGLISH_US);
    }

    public Invocation<T> guildLocale(DiscordLocale locale) {
        when(event.getGuildLocale()).thenReturn(locale);
        return this;
    }

    public Invocation<T> userLocale(DiscordLocale locale) {
        when(event.getUserLocale()).thenReturn(locale);
        return this;
    }

    public Invocation<T> guild(Guild guild) {
        when(event.getGuild()).thenReturn(guild);
        return this;
    }

    public Invocation<T> member(Member member) {
        user = member.getUser();
        when(event.getMember()).thenReturn(member);
        return this;
    }

    public CompletableFuture<MessageCreateData> invoke() {
        eventManager.handle((GenericInteractionCreateEvent) event);
        return reply;
    }
}
