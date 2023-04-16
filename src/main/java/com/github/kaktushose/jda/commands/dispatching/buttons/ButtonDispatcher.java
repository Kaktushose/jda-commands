package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime;

import java.util.Optional;

/**
 * Dispatches commands by taking a {@link ButtonContext} and passing it through the execution chain.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class ButtonDispatcher extends GenericDispatcher<ButtonContext> {

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
        context.getEvent().deferEdit().queue();

        Optional<InteractionRuntime> optional = runtimeSupervisor.getRuntime(context.getEvent());

        if (optional.isEmpty()) {
            context.getEvent().getHook().editOriginalComponents().queue();
            context.getEvent().getHook().sendMessage("*this interaction timed out*").setEphemeral(true).queue();
            return;
        }

        context.getEvent().getHook().sendMessage("button pressed").queue();
    }
}
