package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
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
public abstract class GenericEvent<T extends GenericInteractionDefinition> extends GenericInteractionCreateEvent {

    protected final Context context;
    private final InteractionRegistry interactionRegistry;
    private final T definition;

    /**
     * Constructs a new GenericEvent.
     *
     * @param context the underlying {@link Context}
     */
    @SuppressWarnings("unchecked")
    protected GenericEvent(Context context, InteractionRegistry interactionRegistry) {
        super(context.getEvent().getJDA(), context.getEvent().getResponseNumber(), context.getEvent().getInteraction());
        definition = (T) context.getInteractionDefinition();
        this.context = context;
        this.interactionRegistry = interactionRegistry;
    }

    /**
     * Get the interaction object which describes the component that is executed.
     *
     * @return the underlying interaction object
     */
    public T getInteractionDefinition() {
        return definition;
    }

    /**
     * Get the {@link Context} object.
     *
     * @return the registered {@link Context} object
     */
    public Context getContext() {
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
        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Button");
        }

        String sanitizedId = button.replaceAll("\\.", "");
        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));

        return buttonDefinition.toButton().withId(buttonDefinition.createCustomId(context.getRuntime().getRuntimeId()));
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
     * @param menu  the id of the selectMenu
     * @return a JDA {@link SelectMenu}
     */
    public <S extends SelectMenu> S getSelectMenu(String menu) {
        if (!menu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Select Menu");
        }

        String sanitizedId = menu.replaceAll("\\.", "");
        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        return (S) selectMenuDefinition.toSelectMenu(context.getRuntime().getRuntimeId(), true);
    }
}
