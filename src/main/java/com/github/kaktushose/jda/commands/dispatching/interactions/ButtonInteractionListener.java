package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.reflect.ButtonDefinition;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ButtonInteractionListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ButtonInteractionListener.class);
    private final List<ButtonDefinition> buttons;

    public ButtonInteractionListener() {
        buttons = new ArrayList<>();
    }

    public void addButtons(Collection<ButtonDefinition> buttons) {
        this.buttons.addAll(buttons);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Optional<ButtonDefinition> optional = findById(event.getButton().getId());
        if (!optional.isPresent()) {
            return;
        }
        ButtonDefinition button = optional.get();
        log.info("Executing button interaction {} for user {}", button.getMethod().getName(), event.getUser());
        try {
            button.getMethod().invoke(button.getInstance(), event);
        } catch (Exception e) {
            log.error("Command execution failed!", new InvocationTargetException(e));
        }
    }

    private Optional<ButtonDefinition> findById(String id) {
        return buttons.stream().filter(button -> button.getId().equals(id)).findFirst();
    }

}
