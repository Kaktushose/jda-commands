package io.github.kaktushose.jdac.testing.reply.internal;

import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.invocation.internal.ReplyableInvocation;
import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
import io.github.kaktushose.jdac.testing.reply.ModalEventReply;

public abstract sealed class EventReply permits MessageEventReply, ModalEventReply {

    protected final ReplyableInvocation<?> invocation;
    protected final TestScenario.Context context;

    public EventReply(ReplyableInvocation<?> invocation, TestScenario.Context context) {
        this.invocation = invocation;
        this.context = context;
    }
}
