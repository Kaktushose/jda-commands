package internal.invocation;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import static org.mockito.Mockito.*;

public final class ButtonInvocation extends Invocation<ButtonInteractionEvent> {

    public ButtonInvocation(IEventManager eventManager, String customId) {
        super(eventManager, ButtonInteractionEvent.class);

        when(event.getComponentId()).thenReturn(customId);
        lenient().when(event.deferEdit()).thenReturn(mock(MessageEditCallbackAction.class));
    }
}
