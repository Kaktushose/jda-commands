package framework.reply;

import framework.TestScenario.Context;
import framework.invocation.Invocation;
import framework.invocation.ModalInvocation;
import net.dv8tion.jda.api.interactions.modals.Modal;

public final class ModalEventReply extends AbstractReply {

    private final Modal modal;

    public ModalEventReply(Invocation<?> invocation, Context context, Modal modal) {
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
