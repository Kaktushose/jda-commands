package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Dispatches commands by taking a {@link ButtonContext} and passing it through the execution chain.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class ButtonDispatcher extends GenericDispatcher<ButtonContext> {

    private static final Logger log = LoggerFactory.getLogger(ButtonDispatcher.class);
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new ButtonDispatcher.
     *
     * @param supervisor        the {@link DispatcherSupervisor} which supervises this dispatcher.
     * @param runtimeSupervisor the corresponding {@link RuntimeSupervisor}
     */
    public ButtonDispatcher(DispatcherSupervisor supervisor, RuntimeSupervisor runtimeSupervisor) {
        super(supervisor);
        this.runtimeSupervisor = runtimeSupervisor;
    }

    /**
     * Dispatches a {@link ButtonContext}.
     *
     * @param context the {@link ButtonContext} to dispatch.
     */
    @Override
    public void onEvent(ButtonContext context) {
        log.debug("Acknowledging event");
        context.getEvent().deferEdit().queue();

        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        Optional<InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(context.getEvent());
        if (optionalRuntime.isEmpty()) {
            context.getEvent().getHook().editOriginalComponents().queue();
            context.getEvent().getHook().sendMessage("*this interaction timed out*").setEphemeral(true).queue();
            return;
        }
        InteractionRuntime runtime = optionalRuntime.get();
        log.debug("Found corresponding runtime with id \"{}\"", runtime);

        String[] splitId = context.getEvent().getButton().getId().split("\\.");
        String buttonId = String.format("%s.%s", splitId[0], splitId[1]);
        Optional<ButtonDefinition> optionalButton = interactionRegistry.getButtons().stream()
                .filter(it -> it.getId().equals(buttonId))
                .findFirst();
        if (optionalButton.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No button found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        ButtonDefinition button = optionalButton.get();
        context.setButton(button).setEphemeral(button.isEphemeral());
        log.debug("Input matches button: {}", button);

        log.info("Executing button {} for user {}", button.getMethod().getName(), context.getEvent().getMember());
        try {
            context.setRuntime(runtime);
            button.getMethod().invoke(runtime.getInstance(), new ButtonEvent(button, context));
        } catch (Exception exception) {
            log.error("Button execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkCancelled(ButtonContext context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.queue();
            return true;
        }
        return false;
    }
}
