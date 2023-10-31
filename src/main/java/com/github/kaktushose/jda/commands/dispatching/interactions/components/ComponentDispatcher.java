package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.menus.StringSelectMenuDefinition;
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
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class ComponentDispatcher extends GenericDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ComponentDispatcher.class);

    /**
     * Constructs a new ComponentDispatcher.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance.
     */
    public ComponentDispatcher(JDACommands jdaCommands) {
        super(jdaCommands);
    }

    @Override
    public void onEvent(Context context) {
        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();
        GenericComponentInteractionCreateEvent event = (GenericComponentInteractionCreateEvent) context.getEvent();

        Optional<RuntimeSupervisor.InteractionRuntime> optionalRuntime = runtimeSupervisor.getRuntime(event);
        if (optionalRuntime.isEmpty()) {
            event.getHook().editOriginalComponents().queue();
            event.getHook().sendMessage(messageFactory.getUnknownInteractionMessage(context)).setEphemeral(true).queue();
            return;
        }
        RuntimeSupervisor.InteractionRuntime runtime = optionalRuntime.get();
        log.debug("Found corresponding runtime with id \"{}\"", runtime.getInstanceId());

        String[] splitId = event.getComponentId().split("\\.");
        String componentId = String.format("%s.%s", splitId[0], splitId[1]);

        List<EphemeralInteractionDefinition> components = new ArrayList<>();
        components.addAll(interactionRegistry.getButtons());
        components.addAll(interactionRegistry.getSelectMenus());

        Optional<EphemeralInteractionDefinition> optionalComponent = components.stream().filter(it -> it.getId().equals(componentId)).findFirst();
        if (optionalComponent.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No select menu found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        EphemeralInteractionDefinition component = optionalComponent.get();
        context.setInteractionDefinition(component).setEphemeral(component.isEphemeral());
        log.debug("Input matches component: {}", component.getId());

        log.info("Executing select component {} for user {}", component.getMethod().getName(), event.getMember());
        context.setRuntime(runtime);
        try {
            Class<?> clazz = component.getClass();
            if (EntitySelectMenuDefinition.class.isAssignableFrom(clazz)) {
                component.getMethod().invoke(
                        runtime.getInstance(),
                        new ComponentEvent<EntitySelectMenuDefinition>(context),
                        ((EntitySelectInteractionEvent) event).getMentions()
                );
            } else if (StringSelectMenuDefinition.class.isAssignableFrom(clazz)) {
                component.getMethod().invoke(
                        runtime.getInstance(),
                        new ComponentEvent<EntitySelectMenuDefinition>(context),
                        ((StringSelectInteractionEvent) event).getValues()
                );
            } else if (ButtonDefinition.class.isAssignableFrom(clazz)) {
                component.getMethod().invoke(runtime.getInstance(), new ComponentEvent<ButtonDefinition>(context));
            } else {
                throw new IllegalStateException("Unknown component type! Please report this error the the devs of jda-commands.");
            }
        } catch (Exception exception) {
            log.error("Select menu execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;

            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("ConstantConditions")
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
