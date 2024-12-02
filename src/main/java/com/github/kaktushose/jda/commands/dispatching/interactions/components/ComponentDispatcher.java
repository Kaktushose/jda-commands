package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.StringSelectMenuDefinition;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dispatches component events.
 *
 * @since 4.0.0
 */
public class ComponentDispatcher extends GenericDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ComponentDispatcher.class);

    /**
     * Constructs a new ComponentDispatcher.
     *
     * @param middlewareRegistry
     * @param implementationRegistry
     * @param interactionRegistry
     * @param adapterRegistry
     * @param runtimeSupervisor
     */
    public ComponentDispatcher(MiddlewareRegistry middlewareRegistry, ImplementationRegistry implementationRegistry, InteractionRegistry interactionRegistry, TypeAdapterRegistry adapterRegistry, RuntimeSupervisor runtimeSupervisor) {
        super(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, runtimeSupervisor);
    }

    @Override
    public void onEvent(Context context) {
        GenericComponentInteractionCreateEvent event = (GenericComponentInteractionCreateEvent) context.getEvent();
        if (!event.getComponentId().matches(CustomId.CUSTOM_ID_REGEX)) {
            log.debug("Ignoring non jda-commands event {}", event.getComponentId());
            return;
        }

        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        Optional<RuntimeSupervisor.InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(event);
        if (optionalRuntime.isEmpty()) {
            event.deferEdit().setComponents().queue();
            event.getHook().sendMessage(messageFactory.getUnknownInteractionMessage(context)).setEphemeral(true).queue();
            return;
        }
        RuntimeSupervisor.InteractionRuntime runtime = optionalRuntime.get();
        log.debug("Found corresponding runtime with id \"{}\"", runtime.getRuntimeId());

        String componentId = event.getComponentId().split("\\.")[1];

        List<EphemeralInteractionDefinition> components = new ArrayList<>();
        components.addAll(interactionRegistry.getButtons());
        components.addAll(interactionRegistry.getSelectMenus());

        Optional<EphemeralInteractionDefinition> optionalComponent = components.stream().filter(it -> it.getDefinitionId().equals(componentId)).findFirst();
        if (optionalComponent.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No component found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        EphemeralInteractionDefinition component = optionalComponent.get();
        context.setInteractionDefinition(component).setEphemeral(component.isEphemeral());

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        log.debug("Input matches component: {}", component.getDefinitionId());
        log.info("Executing component {} for user {}", component.getMethod().getName(), event.getMember());
        context.setRuntime(runtime);
        try {
            Class<?> clazz = component.getClass();
            if (EntitySelectMenuDefinition.class.isAssignableFrom(clazz)) {
                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent(context), ((EntitySelectInteractionEvent) event).getMentions());
            } else if (StringSelectMenuDefinition.class.isAssignableFrom(clazz)) {
                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent(context), ((StringSelectInteractionEvent) event).getValues());
            } else if (ButtonDefinition.class.isAssignableFrom(clazz)) {
                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent(context));
            } else {
                throw new IllegalStateException("Unknown component type! Please report this error the the devs of jda-commands.");
            }
        } catch (Exception exception) {
            log.error("Select menu execution failed!", exception);
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
            replyContext.queue();
            return true;
        }
        return false;
    }
}
