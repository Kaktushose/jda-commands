package com.github.kaktushose.jda.commands.dispatching.interactions.contextmenu;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ContextMenuDispatcher extends GenericDispatcher<ContextMenuContext> {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new ContextMenuDispatcher.
     *
     * @param supervisor        the {@link DispatcherSupervisor} which supervises this dispatcher.
     * @param runtimeSupervisor the corresponding {@link RuntimeSupervisor}
     */
    public ContextMenuDispatcher(DispatcherSupervisor supervisor, RuntimeSupervisor runtimeSupervisor) {
        super(supervisor);
        this.runtimeSupervisor = runtimeSupervisor;
    }

    @Override
    public void onEvent(ContextMenuContext context) {
        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        Optional<ContextCommandDefinition> optional = interactionRegistry.getContextCommands().stream()
                .filter(it -> it.getName().equals(context.getEvent().getFullCommandName()))
                .findFirst();

        if (optional.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No context menu command found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        ContextCommandDefinition command = optional.get();
        context.setInteraction(command).setEphemeral(command.isEphemeral());
        log.debug("Input matches command: {}", command);
        log.info("Executing command {} for user {}", command.getMethod().getName(), context.getEvent().getMember());
        try {
            RuntimeSupervisor.InteractionRuntime runtime = runtimeSupervisor.newRuntime(context.getEvent(), command);
            context.setRuntime(runtime);
            ContextMenuEvent event = new ContextMenuEvent(command, context);
            command.getMethod().invoke(runtime.getInstance(), event, context.getEvent().getTarget());
        } catch (Exception exception) {
            log.error("Command execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkCancelled(ContextMenuContext context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.queue();
            return true;
        }
        return false;
    }

}
