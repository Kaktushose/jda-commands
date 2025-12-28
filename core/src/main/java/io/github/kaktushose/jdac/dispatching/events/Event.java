package io.github.kaktushose.jdac.dispatching.events;

import io.github.kaktushose.jdac.dispatching.context.KeyValueStore;
import io.github.kaktushose.jdac.dispatching.events.interactions.AutoCompleteEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
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

import static io.github.kaktushose.jdac.introspection.internal.IntrospectionAccess.*;


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

    /// Returns the underlying [GenericInteractionCreateEvent] of this event
    ///
    /// @return the [GenericInteractionCreateEvent]
    @SuppressWarnings("unchecked")
    public T jdaEvent() {
        return (T) scopedJdaEvent();
    }

    /// Returns the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this event is dispatched in.
    ///
    /// @return the id of the current [`Runtime`]({@docRoot}/index.html#runtime-concept-heading)
    public String runtimeId() {
        return scopedRuntime().id();
    }

    /// Closes the underlying [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). This will ignore any new jda events belonging to this interaction, resulting
    /// in the freeing of occupied resources for gc.
    ///
    /// This is only needed if the expiration strategy
    /// [ExpirationStrategy.Explicit] is used.
    public void closeRuntime() {
        scopedRuntime().close();
    }

    /// Returns the [KeyValueStore] of this [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// The [KeyValueStore] can be accessed during the [Middleware] execution as well as any
    /// interaction execution. Its content will be the same as long as the executions take place in the same [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// @return the [KeyValueStore]
    public KeyValueStore kv() {
        return scopedRuntime().keyValueStore();
    }

    /// Returns an instance of a class annotated with [`Interaction`][io.github.kaktushose.jdac.annotations.interactions.Interaction],
    /// that is bound to the underlying [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    ///
    /// @return the interaction class instance
    @Nullable
    public <I> I interactionInstance(Class<I> interactionClass) {
        return scopedRuntime().interactionInstance(interactionClass);
    }


    /// Gets the [Introspection] instance of this interaction.
    ///
    /// Same as [Introspection#accessScoped()]
    /// @return the [Introspection] instance with stage set to [Stage#INTERACTION].
    public Introspection introspection() {
        return Introspection.accessScoped();
    }

    /// Gets the [MessageResolver] instance
    ///
    /// @return the [MessageResolver] instance
    public MessageResolver messageResolver() {
        return scopedMessageResolver();
    }

    /// Gets a localization message for the given key using the underlying [I18n] instance.
    ///
    /// Automatically resolves the [Locale] using [GenericInteractionCreateEvent#getUserLocale()].
    /// Use [I18n#localize(Locale, String, Entry...)] (obtained via [#i18n()]) if you want to use a different locale.
    ///
    /// @return the localized message or the key if not found
    public String localize(String key, Entry... placeholders) {
        return scopedI18n().localize(jdaEvent().getUserLocale().toLocale(), key, placeholders);
    }

    /// Resolved the given message with help of the underlying [MessageResolver] instance,
    /// thus performing localization and emoji resolution.
    ///
    /// Automatically resolves the [Locale] using [GenericInteractionCreateEvent#getUserLocale()].
    /// Use [MessageResolver#resolve(String, Locale, Entry...)] (obtained via [#messageResolver()]) if you want to use a different locale.
    ///
    /// @return the resolved message
    public String resolve(String message, Entry... placeholders) {
        return messageResolver().resolve(message, jdaEvent().getUserLocale().toLocale(), Entry.toMap(placeholders));
    }


    @Override
    public int getTypeRaw() {
        return jdaEvent().getTypeRaw();
    }

    @Override
    public String getToken() {
        return jdaEvent().getToken();
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return jdaEvent().getGuild();
    }

    @Override
    public User getUser() {
        return jdaEvent().getUser();
    }

    @Nullable
    @Override
    public Member getMember() {
        return jdaEvent().getMember();
    }

    @Override
    public boolean isAcknowledged() {
        return jdaEvent().isAcknowledged();
    }

    @Nullable
    @Override
    public Channel getChannel() {
        return jdaEvent().getChannel();
    }

    @Override
    public long getChannelIdLong() {
        return jdaEvent().getChannelIdLong();
    }

    @Override
    public DiscordLocale getUserLocale() {
        return jdaEvent().getUserLocale();
    }

    @Override
    public List<Entitlement> getEntitlements() {
        return jdaEvent().getEntitlements();
    }

    @Override
    public JDA getJDA() {
        return jdaEvent().getJDA();
    }

    @Override
    public long getIdLong() {
        return jdaEvent().getIdLong();
    }

    @Override
    public IntegrationOwners getIntegrationOwners() {
        return jdaEvent().getIntegrationOwners();
    }

    @Override
    public InteractionContextType getContext() {
        return jdaEvent().getContext();
    }
}
