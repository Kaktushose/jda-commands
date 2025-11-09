package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.FrameworkContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.exceptions.InternalException;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public final class ContextCommandHandler extends EventHandler<GenericContextInteractionEvent<?>> {

    public ContextCommandHandler(FrameworkContext context) {
        super(context);
    }

    @Override
    protected InvocationContext<GenericContextInteractionEvent<?>> prepare(GenericContextInteractionEvent<?> event, Runtime runtime) {
        CommandDefinition command = interactionRegistry.find(ContextCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(command, context.globalReplyConfig());

        Object target = event.getTarget();
        if (event instanceof UserContextInteractionEvent userEvent) {
            target = userEvent.getTargetMember();
            if (target == null) {
                throw new InternalException("null-member-context-command");
            }
        }

        return new InvocationContext<>(
                new InvocationContext.Utility(context.i18n(), context.messageResolver()),
                new InvocationContext.Data<>(event, runtime.keyValueStore(), command, replyConfig,
                        List.of(new CommandEvent(), target)
                )
        );
    }
}
