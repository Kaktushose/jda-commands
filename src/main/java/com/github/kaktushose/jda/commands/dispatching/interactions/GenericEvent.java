package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
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
public abstract class GenericEvent<T extends GenericInteractionDefinition> extends GenericInteractionCreateEvent {

    protected final Context context;
    private final T definition;

    /**
     * Constructs a new GenericEvent.
     *
     * @param context the underlying {@link Context}
     */
    @SuppressWarnings("unchecked")
    protected GenericEvent(Context context) {
        super(context.getEvent().getJDA(), context.getEvent().getResponseNumber(), context.getEvent().getInteraction());
        definition = (T) context.getInteractionDefinition();
        this.context = context;
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
     * Get the {@link JDACommands} object.
     *
     * @return the {@link JDACommands} object
     */
    public JDACommands getJdaCommands() {
        return context.getJdaCommands();
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
        return getJdaCommands().getButton(button, context.getRuntime().getRuntimeId());
    }

    /**
     * Gets a JDA {@link SelectMenu} to use it for message builders based on the jda-commands id. The returned
     * SelectMenu will be linked to the runtime of this event.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a a select menu defined by a
     * {@code onSelectMenu(ComponentEvent event)} method inside an {@code ExampleMenu} class would be
     * {@code ExampleMenu.onSelectMenu}.
     * </p>
     *
     * @param menu the id of the selectMenu
     * @return a JDA {@link SelectMenu}
     */
    public SelectMenu getSelectMenu(String menu) {
        return getJdaCommands().getSelectMenu(menu, context.getRuntime().getRuntimeId());
    }

    /**
     * Gets a JDA {@link SelectMenu} to use it for message builders based on the jda-commands id. The returned
     * SelectMenu will be linked to the runtime of this event.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a a select menu defined by a
     * {@code onSelectMenu(ComponentEvent event)} method inside an {@code ExampleMenu} class would be
     * {@code ExampleMenu.onSelectMenu}.
     * </p>
     *
     * @param menu  the id of the selectMenu
     * @param clazz the subtype of {@link SelectMenu}
     * @return a JDA {@link SelectMenu}
     */
    public <S extends SelectMenu> S getSelectMenu(String menu, Class<S> clazz) {
        return getJdaCommands().getSelectMenu(menu, getContext().getRuntime().getRuntimeId(), clazz);
    }
}
