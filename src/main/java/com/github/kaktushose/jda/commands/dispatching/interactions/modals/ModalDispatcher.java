package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatches {@link ModalInteractionEvent ModalInteractionEvents}.
 *
 * @since 4.0.0
 */
public final class ModalDispatcher extends GenericDispatcher<ModalInteractionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ModalDispatcher.class);

    public ModalDispatcher(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    public void onEvent(ModalInteractionEvent event, Runtime runtime) {

    }

//    @Override
//    public void onEvent(Context context) {
//        ModalInteractionEvent event = (ModalInteractionEvent) context.getEvent();
//        if (!event.getModalId().matches(CustomId.CUSTOM_ID_REGEX)) {
//            log.debug("Ignoring non jda-commands event {}", event.getModalId());
//            return;
//        }
//
//        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();
//
//        Optional<RuntimeSupervisor.InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(event);
//        if (optionalRuntime.isEmpty()) {
//            event.reply(messageFactory.getUnknownInteractionMessage(context)).setEphemeral(true).queue();
//            return;
//        }
//        RuntimeSupervisor.InteractionRuntime runtime = optionalRuntime.get();
//        log.debug("Found corresponding runtime with id \"{}\"", runtime.getRuntimeId());
//
//        String modalId = event.getModalId().split("\\.")[1];
//        Optional<ModalDefinition> optionalModal = interactionRegistry.getModals().stream()
//                .filter(it -> it.getDefinitionId().equals(modalId))
//                .findFirst();
//
//        if (optionalModal.isEmpty()) {
//            IllegalStateException exception = new IllegalStateException(
//                    "No Modal found! Please report this error the the devs of jda-commands."
//            );
//            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, exception));
//            checkCancelled(context);
//            throw exception;
//        }
//
//        ModalDefinition modal = optionalModal.get();
//        context.setInteractionDefinition(modal).setEphemeral(modal.isEphemeral());
//
//        executeMiddlewares(context);
//        if (checkCancelled(context)) {
//            log.debug("Interaction execution cancelled by middleware");
//            return;
//        }
//
//        log.debug("Input matches Modal: {}", modal.getDefinitionId());
//        log.info("Executing Modal {} for user {}", modal.getMethod().getName(), event.getMember());
//        try {
//            context.setRuntime(runtime);
//            List<Object> arguments = new ArrayList<>();
//            arguments.add(new ModalEvent(context, interactionRegistry));
//            arguments.addAll(event.getValues().stream().map(ModalMapping::getAsString).collect(Collectors.toList()));
//            modal.getMethod().invoke(runtime.getInstance(), arguments.toArray());
//        } catch (Exception exception) {
//            log.error("Modal execution failed!", exception);
//            // this unwraps the underlying error in case of an exception inside the command class
//            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
//            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, throwable));
//            checkCancelled(context);
//        }
//    }

//    @SuppressWarnings("DataFlowIssue")
//    private boolean checkCancelled(Context context) {
//        if (context.isCancelled()) {
//            ReplyContext replyContext = new ReplyContext(context);
//            replyContext.getBuilder().applyData(context.getErrorMessage());
//            replyContext.setEditReply(false).queue();
//            return true;
//        }
//        return false;
//    }
}
