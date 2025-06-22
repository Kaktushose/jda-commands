package internal.invocation;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public final class ButtonInvocation extends Invocation<ButtonInteractionEvent> {

    public ButtonInvocation(IEventManager eventManager, String customId, MessageCreateData lastMessage) {
        super(eventManager, ButtonInteractionEvent.class);

        when(event.getComponentId()).thenReturn(customId);
        lenient().when(event.deferEdit()).thenReturn(mock(MessageEditCallbackAction.class));

        Message message = mock(Message.class);
        lenient().when(event.getMessage()).thenReturn(message);
        lenient().when(message.getComponents()).thenReturn(Optional.ofNullable(lastMessage).map(MessageCreateData::getComponents).orElse(List.of()));
    }
}
