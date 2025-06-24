package io.github.kaktushose.jdac.testing.invocation;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.internal.Invocation;
import io.github.kaktushose.jdac.testing.invocation.internal.ReplyableInvocation;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public final class ModalInvocation extends ReplyableInvocation<ModalInteractionEvent> {

    private final List<ModalMapping> modalMappings;

    public ModalInvocation(Context context, String modalId) {
        super(context, ModalInteractionEvent.class, InteractionType.MODAL_SUBMIT);

        lenient().when(event.getModalId()).thenReturn(modalId);

        lenient().when(event.deferEdit()).thenReturn(mock(MessageEditCallbackAction.class));

        modalMappings = new ArrayList<>();
        lenient().when(event.getValues()).thenReturn(modalMappings);
    }

    public ModalInvocation input(String value) {
        ModalMapping mapping = mock(ModalMapping.class);
        when(mapping.getAsString()).thenReturn(value);
        modalMappings.add(mapping);
        return this;
    }

    public ModalInvocation input(String... values) {
        for (String value : values) {
            input(value);
        }
        return this;
    }

}
