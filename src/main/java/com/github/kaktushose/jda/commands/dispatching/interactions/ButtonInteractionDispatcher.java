package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.ButtonEvent;
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

/**
 * Dispatches {@link ButtonInteractionEvent ButtonInteractionEvents}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ButtonEvent
 * @since 2.3.0
 */
public class ButtonInteractionDispatcher extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ButtonInteractionDispatcher.class);
    private final List<ButtonDefinition> buttons;
    private final JDACommands jdaCommands;

    /**
     * Constructs a new ButtonInteractionDispatcher.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public ButtonInteractionDispatcher(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
        buttons = new ArrayList<>();
    }

    /**
     * Adds {@link ButtonDefinition ButtonDefinitions} to the list of buttons the dispatcher is managing.
     *
     * @param buttons the {@link ButtonDefinition ButtonDefinitions} to add
     */
    public void addButtons(Collection<ButtonDefinition> buttons) {
        this.buttons.addAll(buttons);
    }

    /**
     * Dispatches a {@link ButtonInteractionEvent}. If the incoming id matches one of the registered buttons, the button
     * method will be invoked.
     *
     * @param event the incoming {@link ButtonInteractionEvent}
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Optional<ButtonDefinition> optional = findById(event.getButton().getId());
        if (!optional.isPresent()) {
            return;
        }
        ButtonDefinition button = optional.get();
        log.info("Executing button interaction {} for user {}", button.getMethod().getName(), event.getUser());
        try {
            button.getMethod().invoke(button.getInstance(), new ButtonEvent(event, button, jdaCommands));
        } catch (Exception e) {
            log.error("Command execution failed!", new InvocationTargetException(e));
        }
    }

    private Optional<ButtonDefinition> findById(String id) {
        return buttons.stream().filter(button -> button.getId().equals(id)).findFirst();
    }

}
