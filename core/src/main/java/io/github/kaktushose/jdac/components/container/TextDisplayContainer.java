package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Locale;

/// An implementation of [SequencedContainer] that only allows [TextDisplay]s.
///
/// ## Example
/// ```
/// var container = SequencedTextDisplay.of("Line 1");
///
/// container.add("Line 2");
///
/// container.addFirst("Line 0");
/// ```
public final class TextDisplayContainer extends SequencedContainer<TextDisplay> {

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
        super(resolver, locale, header);
    }

    /// Constructs a new [TextDisplayContainer] from the given [String].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param content the [String] to use for the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [JDACScope#PREPARATION] isn't accessible.
    public static TextDisplayContainer of(String content) {
        return of(TextDisplay.of(content));
    }

    /// Constructs a new [TextDisplayContainer] from the given [TextDisplay].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param header the first [TextDisplay] of this container
    /// @throws IllegalStateException if the [JDACScope#PREPARATION] isn't accessible.
    public static TextDisplayContainer of(TextDisplay header) {
        SequencedContainer.checkAccess();
        return new TextDisplayContainer(
                JDACProperty.MESSAGE_RESOLVER.scopedGet(),
                JDACProperty.JDA_EVENT.scopedGet().getUserLocale().toLocale(),
                header
        );
    }

    /// Appends the provided element to the end of this component.
    ///
    /// @param content the content to create the [TextDisplay] from
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer add(String content, Entry... entries) {
        add(TextDisplay.of(content), entries);
        return this;
    }

    /// Adds the provided element to this component as the first element of this component.
    ///
    /// @param content the content to create the [TextDisplay] from
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer addFirst(String content, Entry... entries) {
        addFirst(TextDisplay.of(content), entries);
        return this;
    }

    /// Adds the provided element to this component as the last element of this component.
    ///
    /// @param content the content to create the [TextDisplay] from
    /// @param entries the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public TextDisplayContainer addLast(String content, Entry... entries) {
        addLast(TextDisplay.of(content), entries);
        return this;
    }
}
