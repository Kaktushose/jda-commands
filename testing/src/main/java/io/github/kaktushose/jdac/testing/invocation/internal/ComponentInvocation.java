package io.github.kaktushose.jdac.testing.invocation.internal;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.components.ButtonInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.EntitySelectInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.StringSelectInvocation;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public abstract sealed class ComponentInvocation<T extends GenericComponentInteractionCreateEvent> extends ModalReplyableInvocation<T>
        permits EntitySelectInvocation, ButtonInvocation, StringSelectInvocation {

    public ComponentInvocation(Context context, String customId, @Nullable MessageEditData lastMessage, Class<T> klass) {
        super(context, klass, InteractionType.COMPONENT);

        when(event.getComponentId()).thenReturn(customId);
        when(event.getCustomId()).thenReturn(customId);
        lenient().when(event.deferEdit()).thenReturn(mock(MessageEditCallbackAction.class));

        Message message = mock(Message.class);
        lenient().when(event.getMessage()).thenReturn(message);
        lenient().when(message.getComponents()).thenReturn(Optional.ofNullable(lastMessage).map(MessageEditData::getComponents).orElse(List.of()));
        lenient().when(message.getComponentTree()).then(_ -> ComponentTree.forMessage(message.getComponents()));

        lenient().when(event.replyModal(any(Modal.class))).then(invocation -> {
            modal.complete(invocation.getArgument(0));
            return mock(ModalCallbackAction.class);
        });
    }
}
