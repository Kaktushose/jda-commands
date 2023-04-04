package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime;

import java.util.Optional;

public class ButtonDispatcher extends GenericDispatcher<ButtonContext> {

    private final RuntimeSupervisor runtimeSupervisor;

    public ButtonDispatcher(DispatcherSupervisor dispatcher, RuntimeSupervisor runtimeSupervisor) {
        super(dispatcher);
        this.runtimeSupervisor = runtimeSupervisor;
    }

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
