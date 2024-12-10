package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.modals.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

/**
 * Extension of JDAs {@link GenericInteractionCreateEvent} class. This is the base class for the different event classes.
 *
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent CommandEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent ComponentEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent AutoCompleteEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.modals.ModalEvent ModalEvent
 * @since 4.0.0
 */
public abstract sealed class GenericEvent<T extends GenericInteractionCreateEvent, U extends GenericInteractionDefinition> extends GenericInteractionCreateEvent
        permits AutoCompleteEvent, CommandEvent, ComponentEvent, ModalEvent {

    protected final ExecutionContext<T, U> context;
    private final InteractionRegistry interactionRegistry;

    /**
     * Constructs a new GenericEvent.
     *
     * @param context the underlying {@link Context}
     */
    protected GenericEvent(ExecutionContext<T, U> context, InteractionRegistry interactionRegistry) {
        super(context.event().getJDA(), context.event().getResponseNumber(), context.event().getInteraction());
        this.context = context;
        this.interactionRegistry = interactionRegistry;
    }

    /**
     * Get the {@link Context} object.
     *
     * @return the registered {@link Context} object
     */
    public ExecutionContext<T, U> getContext() {
        return context;
    }

    /**
     * Gets a JDA {@link Button} to use it for message builders based on the jda-commands id. The returned button will
     * be linked to the runtime of this event.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a button defined by a
     * {@code onButton(ComponentEvent event)} method inside an {@code ExampleButton} class would be
     * {@code ExampleButton.onButton}.
     * </p>
     *
     * @param button the id of the button
     * @return a JDA {@link Button}
     */
    public Button getButton(String button) {
        // TODO implement buttons
        throw new UnsupportedOperationException("not supported currently");
//        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
//            throw new IllegalArgumentException("Unknown Button");
//        }
//
//        String sanitizedId = button.replaceAll("\\.", "");
//        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
//                .filter(it -> it.getDefinitionId().equals(sanitizedId))
//                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));
//
//        return buttonDefinition.toButton().withId(buttonDefinition.createCustomId(context.getRuntime().getRuntimeId()));
    }

    /**
     * Gets a JDA {@link SelectMenu} to use it for message builders based on the jda-commands id. The returned
     * SelectMenu will be linked to the runtime of this event.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a select menu defined by a
     * {@code onSelectMenu(ComponentEvent event)} method inside an {@code ExampleMenu} class would be
     * {@code ExampleMenu.onSelectMenu}.
     * </p>
     *
     * @param menu the id of the selectMenu
     * @return a JDA {@link SelectMenu}
     */
    @SuppressWarnings("unchecked")
    public <T extends SelectMenu> T getSelectMenu(String menu) {
        // TODO implement select menus
        throw new UnsupportedOperationException("not supported currently");
//        if (!menu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
//            throw new IllegalArgumentException("Unknown Select Menu");
//        }
//
//        String sanitizedId = menu.replaceAll("\\.", "");
//        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
//                .filter(it -> it.getDefinitionId().equals(sanitizedId))
//                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));
//
//        return (T) selectMenuDefinition.toSelectMenu(context.getRuntime().getRuntimeId(), true);
    }
}
