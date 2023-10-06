package com.github.kaktushose.jda.commands.dispatching.menus;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.EntitySelectMenuDefinition;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class SelectMenuDispatcher extends GenericDispatcher<SelectMenuContext> {

    private static final Logger log = LoggerFactory.getLogger(SelectMenuDispatcher.class);
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new ButtonDispatcher.
     *
     * @param supervisor        the {@link DispatcherSupervisor} which supervises this dispatcher.
     * @param runtimeSupervisor the corresponding {@link RuntimeSupervisor}
     */
    public SelectMenuDispatcher(DispatcherSupervisor supervisor, RuntimeSupervisor runtimeSupervisor) {
        super(supervisor);
        this.runtimeSupervisor = runtimeSupervisor;
    }

    /**
     * Dispatches a {@link ButtonContext}.
     *
     * @param context the {@link ButtonContext} to dispatch.
     */
    @Override
    public void onEvent(SelectMenuContext context) {
        log.debug("Acknowledging event");
        GenericSelectMenuInteractionEvent<?, ?> event = context.getEvent();
        event.deferEdit().queue();

        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        Optional<RuntimeSupervisor.InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(event);
        if (optionalRuntime.isEmpty()) {
            event.getHook().editOriginalComponents().queue();
            event.getHook().sendMessage(messageFactory.getUnknownInteractionMessage(context)).setEphemeral(true).queue();
            return;
        }
        RuntimeSupervisor.InteractionRuntime runtime = optionalRuntime.get();
        log.debug("Found corresponding runtime with id \"{}\"", runtime);

        String[] splitId = event.getComponentId().split("\\.");
        String menuId = String.format("%s.%s", splitId[0], splitId[1]);
        Optional<EntitySelectMenuDefinition> optionalMenu = interactionRegistry.getEntitySelectMenus().stream()
                .filter(it -> it.getId().equals(menuId))
                .findFirst();
        if (optionalMenu.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No button found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        EntitySelectMenuDefinition menu = optionalMenu.get();
        context.setSelectMenu(menu).setEphemeral(menu.isEphemeral());
        log.debug("Input matches button: {}", menu);

        log.info("Executing button {} for user {}", menu.getMethod().getName(), event.getMember());
        try {
            context.setRuntime(runtime);
            menu.getMethod().invoke(runtime.getInstance(), new SelectMenuEvent(menu, context));
        } catch (Exception exception) {
            log.error("Button execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkCancelled(SelectMenuContext context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.queue();
            return true;
        }
        return false;
    }

}
