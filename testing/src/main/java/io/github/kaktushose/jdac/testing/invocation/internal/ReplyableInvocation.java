package io.github.kaktushose.jdac.testing.invocation.internal;

import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.invocation.InvocationException;
import io.github.kaktushose.jdac.testing.invocation.ModalInvocation;
import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public sealed class ReplyableInvocation<T extends IReplyCallback> extends Invocation<T, MessageEventReply>
        permits ModalInvocation, ModalReplyableInvocation {

    protected final CompletableFuture<MessageData> reply = new CompletableFuture<>();
    private MessageEditData lastMessage;

    public ReplyableInvocation(TestScenario.Context context, Class<T> eventClass, InteractionType interactionType) {
        super(context, eventClass, interactionType);

        lenient().when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

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
    }

    protected MessageEventReply complete() {
        try {
            return new MessageEventReply(this, context, reply.get(5, TimeUnit.SECONDS));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new InvocationException(e);
        }
    }

    @Nullable
    public MessageEditData lastMessage() {
        return lastMessage;
    }

}
