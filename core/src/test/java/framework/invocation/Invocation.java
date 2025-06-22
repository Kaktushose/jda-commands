package framework.invocation;

import framework.reply.MessageEventReply;
import framework.TestScenario.Context;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

public abstract sealed class Invocation<T extends IReplyCallback> permits ModalInvocation, ModalReplyableInvocation {

    protected final Context context;
    protected final T event;
    protected final CompletableFuture<MessageData> reply = new CompletableFuture<>();
    private MessageEditData lastMessage;
    private User user;

    public Invocation(Context context, Class<T> eventClass, InteractionType interactionType) {
        this.context = context;

        event = mock(eventClass);
        lenient().when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

        lenient().when(event.getType()).thenReturn(interactionType);

        user = mock(User.class);
        when(event.getUser()).then((_) -> user);

        InteractionHook hook = mock(InteractionHook.class);
        lenient().when(event.getHook()).thenReturn(hook);
        lenient().when(hook.sendMessage(any(MessageCreateData.class))).then(invocation -> {
            reply.complete(invocation.getArgument(0));
            return mock(WebhookMessageCreateAction.class);
        });
        lenient().when(hook.editOriginal(any(MessageEditData.class))).then(invocation -> {
            lastMessage = invocation.getArgument(0);
            reply.complete(invocation.getArgument(0));
            return mock(WebhookMessageEditAction.class);
        });
        lenient().when(hook.setEphemeral(anyBoolean())).thenReturn(hook);

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

    /// Used when an event is replied to with a message or a component.
    public MessageEventReply invoke() {
        context.eventManager().handle((GenericInteractionCreateEvent) event);
        try {
            return new MessageEventReply(this, context, reply.get(5, TimeUnit.SECONDS));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public MessageEditData lastMessage() {
        return lastMessage;
    }
}
