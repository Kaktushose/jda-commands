package framework.reply;

import framework.TestScenario;
import framework.invocation.Invocation;

public abstract sealed class AbstractReply permits EventReply, ModalEventReply {

    protected final Invocation<?> invocation;
    protected final TestScenario.Context context;

    public AbstractReply(Invocation<?> invocation, TestScenario.Context context) {
        this.invocation = invocation;
        this.context = context;
    }
}
