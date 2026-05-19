package io.github.kaktushose.jdac.components;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.components.container.SequencedContainer;
import io.github.kaktushose.jdac.components.internal.LocalizedComponent;
import io.github.kaktushose.jdac.components.internal.SequencedComponent;
import io.github.kaktushose.jdac.exceptions.ReplyException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
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
/// [SequencedTextDisplay#of(String)] uses the [JDACIntrospection] API to access a [Resolver] and the user locale. This
/// means, the static `of` factory method is only usable in [JDACScope#PREPARATION]. If you want to use this class outside
///  JDA-Commands call the constructor and pass the [Resolver] as well as the [Locale] manually.
/// ```
/// var textDisplay = new SequencedTextDisplay(resolver, locale, TextDisplay.of("Hello World!));
/// ```
public final class SequencedTextDisplay
        extends TextDisplayImpl
        implements SequencedComponent<TextDisplay> {

    private final SequencedCollection<TextDisplay> textDisplays;
    private final ComponentResolver<TextDisplay> resolver;
    private final HashMap<TextDisplay, List<Entry>> entries;
    private Locale locale;
    private String delimiter = "\n";

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param content  the [String] to use for the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, DiscordLocale locale, String content, Entry... entries) {
        this(resolver, locale.toLocale(), content);
    }

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, DiscordLocale locale, TextDisplay header, Entry... entries) {
        this(resolver, locale.toLocale(), header);
    }

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param content  the [String] to use for the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, Locale locale, String content, Entry... entries) {
        this(resolver, locale, TextDisplay.of(content));
    }

    /// Constructs a new [SequencedTextDisplay].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first [TextDisplay] of this container
    public SequencedTextDisplay(Resolver<String> resolver, Locale locale, TextDisplay header, Entry... entries) {
        super("");
        textDisplays = new ArrayList<>();
        textDisplays.add(header);
        this.resolver = new ComponentResolver<>(resolver, TextDisplay.class);
        this.entries = new HashMap<>();
        this.entries.put(header, new ArrayList<>(Arrays.asList(entries)));
        this.locale = locale;
    }

    /// Constructs a new [SequencedTextDisplay] from the given [String].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param content the [String] to use for the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [JDACScope#PREPARATION] isn't accessible.
    public static SequencedTextDisplay of(String content, Entry... entries) {
        return of(TextDisplay.of(content), entries);
    }

    /// Constructs a new [SequencedTextDisplay] from the given [TextDisplay].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param header the first [TextDisplay] of this container
    /// @throws ReplyException if the [JDACScope#PREPARATION] isn't accessible.
    public static SequencedTextDisplay of(TextDisplay header, Entry... entries) {
        if (!JDACIntrospection.accessible()) {
            throw new ReplyException("outside-event-handler");
        }
        return new SequencedTextDisplay(
                JDACProperty.MESSAGE_RESOLVER.scopedGet(),
                JDACProperty.JDA_EVENT.scopedGet().getUserLocale().toLocale(),
                header,
                entries
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
        entries(display, entries);
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
        entries(display, entries);
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
        entries(display, entries);
        textDisplays.addLast(display);
        return this;
    }

    @Override
    public SequencedTextDisplay addAll(Collection<TextDisplay> component, Entry... entries) {
        component.forEach(it -> entries(it, entries));
        textDisplays.addAll(component);
        return this;
    }

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    public SequencedTextDisplay locale(DiscordLocale locale) {
        return locale(locale.toLocale());
    }

    @Override
    public SequencedTextDisplay locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public SequencedTextDisplay entries(Entry... entries) {
        return entries(Arrays.asList(entries));
    }

    @Override
    public SequencedTextDisplay entries(Collection<Entry> entries) {
        this.entries.forEach((_, value) -> value.addAll(entries));
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
        return textDisplays.stream().map(it -> resolver.resolve(it, locale, toMap(it))).toList();
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
                .map(it -> resolver.resolve(it, locale, toMap(it)))
                .map(TextDisplay::getContent)
                .collect(Collectors.joining(delimiter));
    }

    /// Replaces all [TextDisplay]s of this [SequencedTextDisplay] with the specified content.
    ///
    /// @param content The new content
    /// @return The new [SequencedTextDisplay]
    @Override
    public SequencedTextDisplay withContent(String content) {
        return withContent(TextDisplay.of(content));
    }

    /// Replaces all [TextDisplay]s of this [SequencedTextDisplay] with the specified content.
    ///
    /// @param content The new content
    /// @param entries the [Entries][Entry] used for localization
    /// @return The new [SequencedTextDisplay]
    public SequencedTextDisplay withContent(String content, Entry... entries) {
        return withContent(TextDisplay.of(content), entries);
    }

    ///Replaces all [TextDisplay]s of this [SequencedTextDisplay] with the specified [TextDisplay].
    ///
    /// @param display The new [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return The new [SequencedTextDisplay]
    public SequencedTextDisplay withContent(TextDisplay display, Entry... entries) {
        entries(display, entries);
        textDisplays.clear();
        textDisplays.add(display);
        return this;
    }

    @Override
    public SequencedTextDisplay withUniqueId(int uniqueId) {
        return (SequencedTextDisplay) super.withUniqueId(uniqueId);
    }

    private void entries(TextDisplay textDisplay, Entry... entries) {
        this.entries.computeIfAbsent(textDisplay, _ -> new ArrayList<>()).addAll(Arrays.asList(entries));
    }

    private Map<String, @Nullable Object> toMap(TextDisplay textDisplay) {
        return Entry.toMap(entries.get(textDisplay).toArray(Entry[]::new));
    }
}
