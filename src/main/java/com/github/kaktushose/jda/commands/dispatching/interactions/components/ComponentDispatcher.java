package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatches component events.
 *
 * @since 4.0.0
 */
public final class ComponentDispatcher extends GenericDispatcher<GenericComponentInteractionCreateEvent> {

    private static final Logger log = LoggerFactory.getLogger(ComponentDispatcher.class);

    public ComponentDispatcher(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    public void onEvent(GenericComponentInteractionCreateEvent event, Runtime runtime) {

    }

//    @Override
//    public void onEvent(Context context) {
//        GenericComponentInteractionCreateEvent event = (GenericComponentInteractionCreateEvent) context.getEvent();
//        if (!event.getComponentId().matches(CustomId.CUSTOM_ID_REGEX)) {
//            log.debug("Ignoring non jda-commands event {}", event.getComponentId());
//            return;
//        }
//
//        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();
//
//        Optional<RuntimeSupervisor.InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(event);
//        if (optionalRuntime.isEmpty()) {
//            event.deferEdit().setComponents().queue();
//            event.getHook().sendMessage(messageFactory.getUnknownInteractionMessage(context)).setEphemeral(true).queue();
//            return;
//        }
//        RuntimeSupervisor.InteractionRuntime runtime = optionalRuntime.get();
//        log.debug("Found corresponding runtime with id \"{}\"", runtime.getRuntimeId());
//
//        String componentId = event.getComponentId().split("\\.")[1];
//
//        List<EphemeralInteractionDefinition> components = new ArrayList<>();
//        components.addAll(interactionRegistry.getButtons());
//        components.addAll(interactionRegistry.getSelectMenus());
//
//        Optional<EphemeralInteractionDefinition> optionalComponent = components.stream().filter(it -> it.getDefinitionId().equals(componentId)).findFirst();
//        if (optionalComponent.isEmpty()) {
//            IllegalStateException exception = new IllegalStateException(
//                    "No component found! Please report this error the the devs of jda-commands."
//            );
//            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, exception));
//            checkCancelled(context);
//            throw exception;
//        }
//
//        EphemeralInteractionDefinition component = optionalComponent.get();
//        context.setInteractionDefinition(component).setEphemeral(component.isEphemeral());
//
//        executeMiddlewares(context);
//        if (checkCancelled(context)) {
//            log.debug("Interaction execution cancelled by middleware");
//            return;
//        }
//
//        log.debug("Input matches component: {}", component.getDefinitionId());
//        log.info("Executing component {} for user {}", component.getMethod().getName(), event.getMember());
//        context.setRuntime(runtime);
//        try {
//            Class<?> clazz = component.getClass();
//            if (EntitySelectMenuDefinition.class.isAssignableFrom(clazz)) {
//                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent(context, interactionRegistry), ((EntitySelectInteractionEvent) event).getMentions());
//            } else if (StringSelectMenuDefinition.class.isAssignableFrom(clazz)) {
//                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent(context, interactionRegistry), ((StringSelectInteractionEvent) event).getValues());
//            } else if (ButtonDefinition.class.isAssignableFrom(clazz)) {
//                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent(context, interactionRegistry));
//            } else {
//                throw new IllegalStateException("Unknown component type! Please report this error the the devs of jda-commands.");
//            }
//        } catch (Exception exception) {
//            log.error("Select menu execution failed!", exception);
//            // this unwraps the underlying error in case of an exception inside the command class
//            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
//
//            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, throwable));
//            checkCancelled(context);
//        }
//    }

//    @SuppressWarnings("DataFlowIssue")
//    private boolean checkCancelled(Context context) {
//        if (context.isCancelled()) {
//            ReplyContext replyContext = new ReplyContext(context);
//            replyContext.getBuilder().applyData(context.getErrorMessage());
//            replyContext.queue();
//            return true;
//        }
//        return false;
//    }
}
