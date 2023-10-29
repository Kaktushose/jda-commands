package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.buttons.ButtonDispatcher;
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

public class ModalDispatcher extends GenericDispatcher<ModalContext> {

    private static final Logger log = LoggerFactory.getLogger(ButtonDispatcher.class);
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new ButtonDispatcher.
     *
     * @param supervisor        the {@link DispatcherSupervisor} which supervises this dispatcher.
     * @param runtimeSupervisor the corresponding {@link RuntimeSupervisor}
     */
    public ModalDispatcher(DispatcherSupervisor supervisor, RuntimeSupervisor runtimeSupervisor) {
        super(supervisor);
        this.runtimeSupervisor = runtimeSupervisor;
    }
    @Override
    public void onEvent(ModalContext context) {
        ModalInteractionEvent event = context.getEvent();
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
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        ModalDefinition modal = optionalModal.get();
        context.setInteraction(modal).setEphemeral(modal.isEphemeral());
        log.debug("Input matches Modal: {}", modal);
        log.info("Executing Modal {} for user {}", modal.getMethod().getName(), event.getMember());
        try {
            context.setRuntime(runtime);
            List<Object> arguments = new ArrayList<>();
            arguments.add(new ModalEvent(modal, context));
            arguments.addAll(event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toSet()));
            modal.getMethod().invoke(runtime.getInstance(), arguments.toArray());
        } catch (Exception exception) {
            log.error("Modal execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkCancelled(ModalContext context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.setEditReply(false).queue();
            return true;
        }
        return false;
    }
}
