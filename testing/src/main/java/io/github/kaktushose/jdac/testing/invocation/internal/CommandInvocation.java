package io.github.kaktushose.jdac.testing.invocation.internal;

import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.invocation.commands.ContextCommandInvocation;
import io.github.kaktushose.jdac.testing.invocation.commands.SlashCommandInvocation;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

public abstract sealed class CommandInvocation<T extends GenericCommandInteractionEvent> extends ModalReplyableInvocation<T>
        permits SlashCommandInvocation, ContextCommandInvocation {

    public CommandInvocation(TestScenario.Context context, String command, Class<T> klass) {
        super(context, klass, InteractionType.COMMAND);

        when(event.getFullCommandName()).thenReturn(command);

        lenient().when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));

        lenient().when(event.replyModal(any(Modal.class))).then(invocation -> {
            modal.complete(invocation.getArgument(0));
            return mock(ModalCallbackAction.class);
        });
    }

    public CommandInvocation<T> channel(MessageChannelUnion channel) {
        when(event.getChannel()).thenReturn(channel);
        return this;
    }
}
