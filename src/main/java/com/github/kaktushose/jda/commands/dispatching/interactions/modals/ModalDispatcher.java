package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dispatches {@link ModalInteractionEvent ModalInteractionEvents}.
 *
 * @since 4.0.0
 */
public class ModalDispatcher extends GenericDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ModalDispatcher.class);

    /**
     * Constructs a new ModalDispatcher.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance.
     */
    public ModalDispatcher(JDACommands jdaCommands) {
        super(jdaCommands);
    }

    @Override
    public void onEvent(Context context) {
        ModalInteractionEvent event = (ModalInteractionEvent) context.getEvent();
        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        Optional<RuntimeSupervisor.InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(event);
        if (optionalRuntime.isEmpty()) {
            event.getHook().sendMessage(messageFactory.getUnknownInteractionMessage(context)).setEphemeral(true).queue();
            return;
        }
        RuntimeSupervisor.InteractionRuntime runtime = optionalRuntime.get();
        log.debug("Found corresponding runtime with id \"{}\"", runtime.getInstanceId());

        String[] splitId = event.getModalId().split("\\.");
        String modalId = String.format("%s.%s", splitId[0], splitId[1]);
        Optional<ModalDefinition> optionalModal = interactionRegistry.getModals().stream()
                .filter(it -> it.getId().equals(modalId))
                .findFirst();

        if (optionalModal.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No Modal found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        ModalDefinition modal = optionalModal.get();
        context.setInteractionDefinition(modal).setEphemeral(modal.isEphemeral());

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        log.debug("Input matches Modal: {}", modal.getId());
        log.info("Executing Modal {} for user {}", modal.getMethod().getName(), event.getMember());
        try {
            context.setRuntime(runtime);
            List<Object> arguments = new ArrayList<>();
            arguments.add(new ModalEvent(context));
            arguments.addAll(event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toSet()));
            modal.getMethod().invoke(runtime.getInstance(), arguments.toArray());
        } catch (Exception exception) {
            log.error("Modal execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private boolean checkCancelled(Context context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.setEditReply(false).queue();
            return true;
        }
        return false;
    }
}
