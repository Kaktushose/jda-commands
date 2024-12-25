package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.definitions.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
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

import java.util.List;


/// Abstract base event for all interaction events, like [CommandEvent].
///
/// This class also holds the [GenericInteractionCreateEvent] and provides some shortcut methods to directly access its
/// content.
///
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see AutoCompleteEvent
/// @see CommandEvent
/// @see ComponentEvent
/// @see ModalEvent
/// @since 4.0.0
public abstract sealed class Event<T extends GenericInteractionCreateEvent> implements Interaction
        permits ReplyableEvent, AutoCompleteEvent {

    protected final T event;
    protected final InteractionRegistry interactionRegistry;
    private final Runtime runtime;

    /// Constructs a new Event.
    ///
    /// @param event               the subtype [T] of [GenericInteractionCreateEvent]
    /// @param interactionRegistry the corresponding [InteractionRegistry]
    /// @param runtime             the [Runtime] this event lives in
    protected Event(@NotNull T event, @NotNull InteractionRegistry interactionRegistry, @NotNull Runtime runtime) {
        this.event = event;
        this.interactionRegistry = interactionRegistry;
        this.runtime = runtime;
    }

    /// Returns the underlying [GenericInteractionCreateEvent] of this event
    ///
    /// @return the [GenericInteractionCreateEvent]
    @NotNull
    public T jdaEvent() {
        return this.event;
    }

    /// Gets a [`Button`][com.github.kaktushose.jda.commands.annotations.interactions.Button] based on the method name
    /// and transforms it into a JDA [Button].
    ///
    /// The button will be linked to the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). This may be useful if you want to send a component without
    /// using the framework.
    ///
    /// @param button the name of the button
    /// @return the JDA [Button]
    @NotNull
    public Button getButton(@NotNull String button) {
        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Button");
        }

        String sanitizedId = button.replaceAll("\\.", "");
        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));

        return buttonDefinition.toButton().withId(buttonDefinition.boundCustomId(runtimeId()));
    }

    /// Gets a [StringSelectMenu] or [EntitySelectMenu] based on the method name and transforms it into a JDA [SelectMenu].
    ///
    /// The select menu will be linked to the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). This may be useful if you want to send a component
    /// without using the framework.
    ///
    /// @param menu the name of the select menu
    /// @return the JDA [SelectMenu]
    @NotNull
    public SelectMenu getSelectMenu(@NotNull String menu) {
        if (!menu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Select Menu");
        }

        String sanitizedId = menu.replaceAll("\\.", "");
        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        return selectMenuDefinition.toSelectMenu(runtimeId(), true);
    }

    /// Returns the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this event is dispatched in.
    ///
    ///
    /// @return the id of the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading)
    @NotNull
    public String runtimeId() {
        return runtime.id();
    }

    /// Closes the underlying [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). This will ignore any new jda events belonging to this interaction, resulting
    /// in the freeing of occupied resources for gc.
    ///
    /// This is only needed if the expiration strategy
    /// [com.github.kaktushose.jda.commands.dispatching.ExpirationStrategy.Explicit] is used.
    public void closeRuntime() {
        runtime.close();
    }

    /// Returns the [KeyValueStore] of this [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// The [KeyValueStore] can be accessed during the [Middleware] execution as well as any
    /// interaction execution. Its content will be the same as long as the executions take place in the same [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// @return the [KeyValueStore]
    @NotNull
    public KeyValueStore kv() {
        return runtime.keyValueStore();
    }


    @Override
    public int getTypeRaw() {
        return event.getTypeRaw();
    }

    @NotNull
    @Override
    public String getToken() {
        return event.getToken();
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @NotNull
    @Override
    public User getUser() {
        return event.getUser();
    }

    @Nullable
    @Override
    public Member getMember() {
        return event.getMember();
    }

    @Override
    public boolean isAcknowledged() {
        return event.isAcknowledged();
    }

    @Nullable
    @Override
    public Channel getChannel() {
        return event.getChannel();
    }

    @Override
    public long getChannelIdLong() {
        return event.getChannelIdLong();
    }

    @NotNull
    @Override
    public DiscordLocale getUserLocale() {
        return event.getUserLocale();
    }

    @NotNull
    @Override
    public List<Entitlement> getEntitlements() {
        return event.getEntitlements();
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return event.getJDA();
    }

    @Override
    public long getIdLong() {
        return event.getIdLong();
    }
}
