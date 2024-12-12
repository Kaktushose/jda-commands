package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of JDAs {@link GenericInteractionCreateEvent} class. This is the base class for the different event classes.
 *
 * @see CommandEvent CommandEvent
 * @see ComponentEvent ComponentEvent
 * @see AutoCompleteEvent AutoCompleteEvent
 * @see ModalEvent ModalEvent
 * @since 4.0.0
 */
public abstract sealed class Event<T extends GenericInteractionCreateEvent>
        permits ReplyableEvent, AutoCompleteEvent {

    protected final T event;
    protected final InteractionRegistry interactionRegistry;
    protected final Runtime runtime;

    /**
     * Constructs a new GenericEvent.
     *
     */
    protected Event(T event, InteractionRegistry interactionRegistry, Runtime runtime) {
        this.event = event;
        this.interactionRegistry = interactionRegistry;
        this.runtime = runtime;
    }

    @NotNull
    public Interaction getInteraction()
    {
        return event.getInteraction();
    }

    @Nullable
    public Guild getGuild()
    {
        return getInteraction().getGuild();
    }

    @Nullable
    public Channel getChannel()
    {
        return getInteraction().getChannel();
    }

    public long getChannelIdLong()
    {
        return getInteraction().getChannelIdLong();
    }

    @NotNull
    public DiscordLocale getUserLocale()
    {
        return getInteraction().getUserLocale();
    }

    @NotNull
    public DiscordLocale getGuildLocale()
    {
        return getInteraction().getGuildLocale();
    }

    @Nullable
    public Member getMember()
    {
        return getInteraction().getMember();
    }

    @NotNull
    public User getUser()
    {
        return getInteraction().getUser();
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

        return buttonDefinition.toButton().withId(buttonDefinition.createCustomId(runtimeId()));
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
    public <S extends SelectMenu> S getSelectMenu(String menu) {
        if (!menu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Select Menu");
        }

        String sanitizedId = menu.replaceAll("\\.", "");
        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        return (S) selectMenuDefinition.toSelectMenu(runtimeId(), true);
    }

    protected InteractionRegistry interactionRegistry() {
        return interactionRegistry;
    }

    public String runtimeId() {
        return runtime.id();
    }

    public KeyValueStore kv() {
        return runtime.keyValueStore();
    }
}
