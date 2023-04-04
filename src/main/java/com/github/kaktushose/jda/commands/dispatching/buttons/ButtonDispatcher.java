package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.GenericDispatcher;

public class ButtonDispatcher extends GenericDispatcher<ButtonContext> {


    public ButtonDispatcher(DispatcherSupervisor supervisor) {
        super(supervisor);
    }

    @Override
    public void onEvent(ButtonContext context) {

    }
}
