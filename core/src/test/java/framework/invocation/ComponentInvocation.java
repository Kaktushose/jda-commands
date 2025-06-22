package framework.invocation;

import framework.TestScenario.Context;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public abstract sealed class ComponentInvocation<T extends GenericComponentInteractionCreateEvent> extends Invocation<T>
        permits EntitySelectInvocation, ButtonInvocation, StringSelectInvocation {

    public ComponentInvocation(Context context, String customId, @Nullable MessageEditData lastMessage, Class<T> klass) {
        super(context, klass);

        when(event.getComponentId()).thenReturn(customId);
        lenient().when(event.deferEdit()).thenReturn(mock(MessageEditCallbackAction.class));

        Message message = mock(Message.class);
        lenient().when(event.getMessage()).thenReturn(message);
        lenient().when(message.getComponents()).thenReturn(Optional.ofNullable(lastMessage).map(MessageEditData::getComponents).orElse(List.of()));
    }
}
