package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.KeyValueStore;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.message.MessageResolver;
import com.github.kaktushose.jda.commands.message.i18n.I18n;
import com.github.kaktushose.jda.commands.message.placeholder.Entry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;


/// Abstract base event for all interaction events, like [CommandEvent].
///
/// This class also holds the [GenericInteractionCreateEvent] and provides some shortcut methods to directly access its
/// content.
///
/// @param <T> the type of [GenericInteractionCreateEvent] this event represents
/// @see AutoCompleteEvent
/// @see CommandEvent
/// @see ComponentEvent
/// @see ModalEvent
public abstract sealed class Event<T extends GenericInteractionCreateEvent> implements Interaction
        permits ReplyableEvent, AutoCompleteEvent {

    protected final T event;
    protected final Runtime runtime;

    /// Constructs a new Event.
    ///
    /// @param event    the subtype [T] of [GenericInteractionCreateEvent]
    /// @param runtime  the [Runtime] this event lives in
    protected Event(T event, Runtime runtime) {
        this.event = event;
        this.runtime = runtime;
    }

    /// Returns the underlying [GenericInteractionCreateEvent] of this event
    ///
    /// @return the [GenericInteractionCreateEvent]
    public T jdaEvent() {
        return this.event;
    }

    /// Returns the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this event is dispatched in.
    ///
    /// @return the id of the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading)
    public String runtimeId() {
        return runtime.id();
    }

    /// Closes the underlying [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). This will ignore any new jda events belonging to this interaction, resulting
    /// in the freeing of occupied resources for gc.
    ///
    /// This is only needed if the expiration strategy
    /// [ExpirationStrategy.Explicit] is used.
    public void closeRuntime() {
        runtime.close();
    }

    /// Returns the [KeyValueStore] of this [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// The [KeyValueStore] can be accessed during the [Middleware] execution as well as any
    /// interaction execution. Its content will be the same as long as the executions take place in the same [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// @return the [KeyValueStore]
    public KeyValueStore kv() {
        return runtime.keyValueStore();
    }

    /// Returns an instance of a class annotated with [`Interaction`][com.github.kaktushose.jda.commands.annotations.interactions.Interaction],
    /// that is bound to the underlying [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// @return the interaction class instance
    @Nullable
    public <I> I interactionInstance(Class<I> interactionClass) {
        return runtime.interactionInstance(interactionClass);
    }


    /// Gets the [I18n] instance.
    ///
    /// @return the [I18n] instance.
    public I18n i18n() {
        return runtime.holyGrail().i18n();
    }

    /// Gets the [MessageResolver] instance
    ///
    /// @return the [MessageResolver] instance
    public MessageResolver messageResolver() {
        return runtime.holyGrail().messageResolver();
    }

    /// Gets a localization message for the given key using the underlying [I18n] instance.
    ///
    /// Automatically resolves the [Locale] using [GenericInteractionCreateEvent#getUserLocale()].
    /// Use [I18n#localize(Locale, String, Entry...)] (obtained via [#i18n()]) if you want to use a different locale.
    ///
    /// @return the localized message or the key if not found
    public String localize(String key, Entry... placeholders) {
        return i18n().localize(event.getUserLocale().toLocale(), key, placeholders);
    }

    /// Resolved the given message with help of the underlying [MessageResolver] instance,
    /// thus performing localization and emoji resolution.
    ///
    /// Automatically resolves the [Locale] using [GenericInteractionCreateEvent#getUserLocale()].
    /// Use [MessageResolver#resolve(String, Locale, Entry...)] (obtained via [#messageResolver()]) if you want to use a different locale.
    ///
    /// @return the resolved message
    public String resolve(String message, Entry... placeholders) {
        return messageResolver().resolve(message, event.getUserLocale().toLocale(), placeholders);
    }


    @Override
    public int getTypeRaw() {
        return event.getTypeRaw();
    }

    @Override
    public String getToken() {
        return event.getToken();
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

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

    @Override
    public DiscordLocale getUserLocale() {
        return event.getUserLocale();
    }

    @Override
    public List<Entitlement> getEntitlements() {
        return event.getEntitlements();
    }

    @Override
    public JDA getJDA() {
        return event.getJDA();
    }

    @Override
    public long getIdLong() {
        return event.getIdLong();
    }

    @Override
    public IntegrationOwners getIntegrationOwners() {
        return event.getIntegrationOwners();
    }

    @Override
    public InteractionContextType getContext() {
        return event.getContext();
    }
}
