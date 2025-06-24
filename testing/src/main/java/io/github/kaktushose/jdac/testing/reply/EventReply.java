package io.github.kaktushose.jdac.testing.reply;

import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.invocation.Invocation;

public abstract sealed class EventReply permits MessageEventReply, ModalEventReply {

    protected final Invocation<?> invocation;
    protected final TestScenario.Context context;

    public EventReply(Invocation<?> invocation, TestScenario.Context context) {
        this.invocation = invocation;
        this.context = context;
    }
}
