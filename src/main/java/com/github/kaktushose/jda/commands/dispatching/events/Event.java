package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.context.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;
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
public abstract sealed class Event<T extends GenericInteractionCreateEvent> implements Interaction
        permits ReplyableEvent, AutoCompleteEvent {

    protected final T event;
    protected final InteractionRegistry registry;
    private final Runtime runtime;

    /// Constructs a new Event.
    ///
    /// @param event    the subtype [T] of [GenericInteractionCreateEvent]
    /// @param registry the corresponding [InteractionRegistry]
    /// @param runtime  the [Runtime] this event lives in
    protected Event(@NotNull T event, @NotNull InteractionRegistry registry, @NotNull Runtime runtime) {
        this.event = event;
        this.registry = registry;
        this.runtime = runtime;
    }

    /// Returns the underlying [GenericInteractionCreateEvent] of this event
    ///
    /// @return the [GenericInteractionCreateEvent]
    @NotNull
    public T jdaEvent() {
        return this.event;
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
