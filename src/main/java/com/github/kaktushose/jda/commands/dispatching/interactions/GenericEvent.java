package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/**
 * Extension of JDAs {@link GenericInteractionCreateEvent} class. This is the base class for the different event classes.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent CommandEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentEvent ComponentEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent AutoCompleteEvent
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.modals.ModalEvent ModalEvent
 * @since 4.0.0
 */
public abstract class GenericEvent<T extends GenericInteractionDefinition> extends GenericInteractionCreateEvent {

    private final T definition;
    protected final Context context;

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
}
