package io.github.kaktushose.jdac.components.internal;

import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/// Interface for [Component]s that can be localized.
public sealed interface LocalizedComponent permits AbstractSequencedContainer, SequencedComponent {

    /// Gets the [Locale] this container will be localized in
    ///
    /// @return the [Locale]
    Locale locale();

    /// Sets the [Locale] this component will be localized in
    ///
    /// @param locale the new [DiscordLocale] to use for localization
    /// @return this instance for fluent interface
    LocalizedComponent locale(DiscordLocale locale);

    /// Sets the [Locale] this component will be localized in
    ///
    /// @param locale the new [Locale] to use for localization
    /// @return this instance for fluent interface
    LocalizedComponent locale(Locale locale);

    /// Adds [Entries][Entry] to use for localization.
    ///
    /// @param entries the [Entries][Entry]
    /// @return this instance for fluent interface
    LocalizedComponent entries(Entry... entries);

    /// Adds [Entries][Entry] to use for localization.
    ///
    /// @param entries the [Entries][Entry]
    /// @return this instance for fluent interface
    LocalizedComponent entries(Collection<Entry> entries);

}
