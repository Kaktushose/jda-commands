package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.textdisplay.TextDisplayImpl;

import java.util.Locale;
import java.util.SequencedCollection;
import java.util.stream.Collectors;

/// A [TextDisplay] implementation that is backed by a [SequencedContainer] and can be used both as a [TextDisplay]
/// as well as a [Container].
///
/// The [#add(String, Entry...)] methods of this container will directly add to the internally stored
///  [SequencedContainer], which can be accessed via [#textDisplays()]. All other getters, namely [#getContent()] and
/// [#toData()], will unfold the [SequencedContainer] and join all [TextDisplay]s into one.
///
/// If used inside a [SeparatorContainer], this container doesn't get unfolded and all [TextDisplay]s will be added
/// in sequence.
public final class TextDisplayContainer extends TextDisplayImpl {

    private static final ComponentSerializer componentSerializer = new ComponentSerializer();
    private SequencedContainer<TextDisplay> container;
    private String delimiter = "\n";

    /// Constructs a new [TextDisplayContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param content  the [String] to use for the first [TextDisplay] of this container
    public TextDisplayContainer(Resolver<String> resolver, DiscordLocale locale, String content) {
        this(resolver, locale.toLocale(), content);
    }

    /// Constructs a new [TextDisplayContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first [TextDisplay] of this container
    public TextDisplayContainer(Resolver<String> resolver, DiscordLocale locale, TextDisplay header) {
        this(resolver, locale.toLocale(), header);
    }

    /// Constructs a new [TextDisplayContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param content  the [String] to use for the first [TextDisplay] of this container
    public TextDisplayContainer(Resolver<String> resolver, Locale locale, String content) {
        this(resolver, locale, TextDisplay.of(content));
    }

    /// Constructs a new [TextDisplayContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first [TextDisplay] of this container
    public TextDisplayContainer(Resolver<String> resolver, Locale locale, TextDisplay header) {
        super(componentSerializer.serialize(header));
        container = new SequencedContainer<>(resolver, locale, header);
    }

    /// Constructs a new [TextDisplayContainer] from the given [String].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param content the [String] to use for the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [Stage#PREPARATION] isn't accessible.
    public static TextDisplayContainer of(String content) {
        return of(TextDisplay.of(content));
    }

    /// Constructs a new [SequencedContainer] from the given [TextDisplay].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param header the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [Stage#PREPARATION] isn't accessible.
    public static TextDisplayContainer of(TextDisplay header) {
        SequencedContainer.checkAccess();
        return new TextDisplayContainer(
                Introspection.scopedGet(Property.MESSAGE_RESOLVER),
                Introspection.scopedGet(Property.JDA_EVENT).getUserLocale().toLocale(),
                header
        );
    }

    /// Appends a [TextDisplay] to the end of this container.
    ///
    /// @param content the [String] to use for the [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer add(String content, Entry... entries) {
        return add(TextDisplay.of(content), entries);
    }

    /// Appends a [TextDisplay] to the end of this container.
    ///
    /// @param display the [TextDisplay] to add
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer add(TextDisplay display, Entry... entries) {
        container.add(display, entries);
        return this;
    }

    /// Adds a [TextDisplay] to this container as the first [TextDisplay] of this container.
    ///
    /// @param content the [String] to use for [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer addFirst(String content, Entry... entries) {
        return addFirst(TextDisplay.of(content), entries);
    }

    /// Adds a [TextDisplay] to this container as the first [TextDisplay] of this container.
    ///
    /// @param display the [TextDisplay] to add
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer addFirst(TextDisplay display, Entry... entries) {
        container.addFirst(display, entries);
        return this;
    }

    /// Adds a [TextDisplay] to this container as the last [TextDisplay] of this container.
    ///
    /// @param content the [String] to use for the [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer addLast(String content, Entry... entries) {
        return addLast(TextDisplay.of(content), entries);
    }

    /// Adds a [TextDisplay] to this container as the last [TextDisplay] of this container.
    ///
    /// @param display the [TextDisplay] to add
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer addLast(TextDisplay display, Entry... entries) {
        container.addLast(display, entries);
        return this;
    }

    /// Gets the [Locale] this container will be localized in
    ///
    /// @return the [Locale]
    public Locale locale() {
        return container.locale();
    }

    /// Sets the [Locale] this container will be localized in
    ///
    /// @param locale the new [Locale] to use for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer locale(Locale locale) {
        container.locale(locale);
        return this;
    }

    /// Sets the [Locale] this container will be localized in
    ///
    /// @param locale the new [DiscordLocale] to use for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer locale(DiscordLocale locale) {
        container.locale(locale);
        return this;
    }

    /// Sets the delimiter used for joining the [TextDisplay]s. Defaults to `\n`.
    ///
    /// @param delimiter the delimiter to use, see [String#join(CharSequence, CharSequence...)]
    public void delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /// Gets all [TextDisplay]s of this container.
    ///
    /// @return the [TextDisplay]s of this container
    public SequencedCollection<TextDisplay> textDisplays() {
        return container.getComponents().stream().map(TextDisplay.class::cast).toList();
    }

    /// Serialized [DataObject] for this container.
    ///
    /// This will join all [TextDisplay]s of this container into one.
    ///
    /// @return the [DataObject]
    @Override
    public DataObject toData() {
        DataObject json = DataObject.empty().put("type", getType().getKey()).put("content", getContent());
        if (getUniqueId() >= 0) {
            json.put("id", getUniqueId());
        }
        return json;
    }

    /// Gets the content of all [TextDisplay]s of this container joined together.
    ///
    /// @return the joined content
    @Override
    public String getContent() {
        return container.getComponents().stream()
                .map(TextDisplay.class::cast)
                .map(TextDisplay::getContent)
                .collect(Collectors.joining(delimiter));
    }

    /// Creates a new [TextDisplayContainer] with the specified content. This replaces any [TextDisplay] added before.
    ///
    /// @param content The new content
    /// @return The new [TextDisplayContainer]
    @Override
    public TextDisplayContainer withContent(String content) {
        return withContent(TextDisplay.of(content));
    }

    /// Creates a new [TextDisplayContainer] with the specified content. This replaces any [TextDisplay] added before.
    ///
    /// @param content The new content
    /// @param entries the [Entries][Entry] used for localization
    /// @return The new [TextDisplayContainer]
    public TextDisplayContainer withContent(String content, Entry... entries) {
        return withContent(TextDisplay.of(content), entries);
    }

    /// Creates a new [TextDisplayContainer] with the specified [TextDisplay]. This replaces any [TextDisplay] added before.
    ///
    /// @param display The new [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return The new [TextDisplayContainer]
    public TextDisplayContainer withContent(TextDisplay display, Entry... entries) {
        container = container.withComponents(display);
        container.entries(entries);
        return this;
    }

    @Override
    public TextDisplayContainer withUniqueId(int uniqueId) {
        return (TextDisplayContainer) super.withUniqueId(uniqueId);
    }
}
