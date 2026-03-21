package io.github.kaktushose.jdac.components;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.components.container.SequencedContainer;
import io.github.kaktushose.jdac.components.internal.SequencedComponent;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.textdisplay.TextDisplayImpl;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/// A [TextDisplay] implementation that is backed by a [java.util.SequencedCollection] and allows its content to be
/// appended in sequence.
///
/// The [#add(String, Entry...)] methods of this class will first append to an internally stored
/// [java.util.SequencedCollection]. When [#getContent()] or [#toData()] gets called, this collection gets unfolded and
/// all [TextDisplay]s are joined into one. [#textDisplays()] will return the [TextDisplay]s directly as stored in the
/// [java.util.SequencedCollection].
///
/// ## Usage inside [SequencedContainer]s
/// If used inside a [SequencedContainer], the [TextDisplay]s don't get joined and each [TextDisplay] will be added
/// individually in sequence.
///
/// ## Localization
/// This [TextDisplay] implementation also supports localization. When [#getContent()()] or [#toData()] is called, the
/// content of this [TextDisplay] is localized via [ComponentResolver]. Use [#entries(Entry...)] to provide
/// additional [Entries][Entry] outside the [#add(String, Entry...)] methods.
///
/// ## Usage outside JDA-Commands
/// [SequencedTextDisplay#of(String)] uses the [Introspection] API to access a [Resolver] and the user locale. This
/// means, the static `of` factory method is only usable in [Stage#PREPARATION]. If you want to use this class outside
///  JDA-Commands call the constructor and pass the [Resolver] as well as the [Locale] manually.
/// ```
/// var textDisplay = new SequencedTextDisplay(resolver, locale, TextDisplay.of("Hello World!));
/// ```
public final class SequencedTextDisplay
        extends TextDisplayImpl
        implements SequencedComponent<TextDisplay> {

    private static final ComponentSerializer componentSerializer = new ComponentSerializer();
    private final SequencedCollection<TextDisplay> textDisplays;
    private final ComponentResolver<TextDisplay> resolver;
    private final List<Entry> entries;
    private Locale locale;
    private String delimiter = "\n";

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param content  the [String] to use for the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, DiscordLocale locale, String content) {
        this(resolver, locale.toLocale(), content);
    }

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, DiscordLocale locale, TextDisplay header) {
        this(resolver, locale.toLocale(), header);
    }

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param content  the [String] to use for the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, Locale locale, String content) {
        this(resolver, locale, TextDisplay.of(content));
    }

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, Locale locale, TextDisplay header) {
        super(componentSerializer.serialize(header));
        textDisplays = new ArrayList<>();
        this.resolver = new ComponentResolver<>(resolver, TextDisplay.class);
        entries = new ArrayList<>();
        this.locale = locale;
    }

    /// Constructs a new [SequencedTextDisplay] from the given [String].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param content the [String] to use for the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [Stage#PREPARATION] isn't accessible.
    public static SequencedTextDisplay of(String content) {
        return of(TextDisplay.of(content));
    }

    /// Constructs a new [SequencedTextDisplay] from the given [TextDisplay].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param header the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [Stage#PREPARATION] isn't accessible.
    public static SequencedTextDisplay of(TextDisplay header) {
        if (!Introspection.accessible()) {
            throw new IllegalStateException("TODO: Illegal call outside of of event handler");
        }
        return new SequencedTextDisplay(
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
    public SequencedTextDisplay add(String content, Entry... entries) {
        return add(TextDisplay.of(content), entries);
    }

    /// Appends a [TextDisplay] to the end of this container.
    ///
    /// @param display the [TextDisplay] to add
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SequencedTextDisplay add(TextDisplay display, Entry... entries) {
        entries(entries);
        textDisplays.add(display);
        return this;
    }

    /// Adds a [TextDisplay] to this container as the first [TextDisplay] of this container.
    ///
    /// @param content the [String] to use for [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SequencedTextDisplay addFirst(String content, Entry... entries) {
        return addFirst(TextDisplay.of(content), entries);
    }

    /// Adds a [TextDisplay] to this container as the first [TextDisplay] of this container.
    ///
    /// @param display the [TextDisplay] to add
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SequencedTextDisplay addFirst(TextDisplay display, Entry... entries) {
        entries(entries);
        textDisplays.addFirst(display);
        return this;
    }

    /// Adds a [TextDisplay] to this container as the last [TextDisplay] of this container.
    ///
    /// @param content the [String] to use for the [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SequencedTextDisplay addLast(String content, Entry... entries) {
        return addLast(TextDisplay.of(content), entries);
    }

    /// Adds a [TextDisplay] to this container as the last [TextDisplay] of this container.
    ///
    /// @param display the [TextDisplay] to add
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SequencedTextDisplay addLast(TextDisplay display, Entry... entries) {
        entries(entries);
        textDisplays.addLast(display);
        return this;
    }

    @Override
    public SequencedTextDisplay addAll(Collection<TextDisplay> component, Entry... entries) {
        entries(entries);
        textDisplays.addAll(component);
        return this;
    }

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    public SequencedTextDisplay locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public SequencedTextDisplay entries(Collection<Entry> entries) {
        this.entries.addAll(entries);
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
        return textDisplays.stream().map(it -> resolver.resolve(it, locale, toMap())).toList();
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
        return textDisplays.stream()
                .map(it -> resolver.resolve(it, locale, toMap()))
                .map(TextDisplay::getContent)
                .collect(Collectors.joining(delimiter));
    }

    /// Creates a new [SequencedTextDisplay] with the specified content. This replaces any [TextDisplay] added before.
    ///
    /// @param content The new content
    /// @return The new [SequencedTextDisplay]
    @Override
    public SequencedTextDisplay withContent(String content) {
        return withContent(TextDisplay.of(content));
    }

    /// Creates a new [SequencedTextDisplay] with the specified content. This replaces any [TextDisplay] added before.
    ///
    /// @param content The new content
    /// @param entries the [Entries][Entry] used for localization
    /// @return The new [SequencedTextDisplay]
    public SequencedTextDisplay withContent(String content, Entry... entries) {
        return withContent(TextDisplay.of(content), entries);
    }

    /// Creates a new [SequencedTextDisplay] with the specified [TextDisplay]. This replaces any [TextDisplay] added before.
    ///
    /// @param display The new [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return The new [SequencedTextDisplay]
    public SequencedTextDisplay withContent(TextDisplay display, Entry... entries) {
        entries(entries);
        textDisplays.clear();
        textDisplays.add(display);
        return this;
    }

    @Override
    public SequencedTextDisplay withUniqueId(int uniqueId) {
        return (SequencedTextDisplay) super.withUniqueId(uniqueId);
    }

    private Map<String, @Nullable Object> toMap() {
        return Entry.toMap(entries.toArray(Entry[]::new));
    }
}
