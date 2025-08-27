package io.github.kaktushose.jdac.testing.reply;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.ModalInvocation;
import io.github.kaktushose.jdac.testing.invocation.internal.ReplyableInvocation;
import io.github.kaktushose.jdac.testing.reply.internal.EventReply;
import net.dv8tion.jda.api.interactions.modals.Modal;

public final class ModalEventReply extends EventReply {

    private final Modal modal;

    public ModalEventReply(ReplyableInvocation<?> invocation, Context context, Modal modal) {
        super(invocation, context);
        this.modal = modal;
    }

    public Modal modal() {
        return modal;
    }

    public ModalInvocation submit() {
        return new ModalInvocation(context, modal.getId());
    }
}
