package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;

/// An implementation of [SequencedContainer] that always adds a [Separator] between its elements.
public final class SeparatedContainer extends SequencedContainer<ContainerChildComponent> {

    private final Separator separator;

    /// Constructs a new [SeparatedContainer].
    ///
    /// @param resolver  the [Resolver] to use for localization
    /// @param locale    the locale this container will be localized to
    /// @param header    the first component of this container
    /// @param separator the [Separator] to use to divide elements
    public SeparatedContainer(Resolver<String> resolver, DiscordLocale locale, ContainerChildComponent header, Separator separator) {
        this(resolver, locale.toLocale(), header, separator);
    }

    /// Constructs a new [SeparatedContainer].
    ///
    /// @param resolver  the [Resolver] to use for localization
    /// @param locale    the locale this container will be localized to
    /// @param header    the first component of this container
    /// @param separator the [Separator] to use to divide elements
    public SeparatedContainer(Resolver<String> resolver, Locale locale, ContainerChildComponent header, Separator separator) {
        super(resolver, locale, header);
        this.separator = separator;
    }

    /// Constructs a new [SeparatedContainer] from the given component.
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param header    the first component of this container
    /// @param separator the [Separator] to use to divide elements
    /// @throws IllegalStateException if the [JDACScope#PREPARATION] isn't accessible.
    public static SeparatedContainer of(ContainerChildComponent header, Separator separator) {
        SequencedContainer.checkAccess();
        return new SeparatedContainer(
                JDACProperty.MESSAGE_RESOLVER.scopedGet(),
                JDACProperty.JDA_EVENT.scopedGet().getUserLocale().toLocale(),
                header,
                separator
        );
    }

    /// {@inheritDoc} Automatically appends the default [Separator] after the component. Use
    /// [#add(ContainerChildComponent, Separator, Entry...)] to override.
    ///
    /// @param component the component to add
    /// @param entries   {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public SeparatedContainer add(ContainerChildComponent component, Entry... entries) {
        super.add(component, entries);
        super.add(separator);
        return this;
    }

    /// Appends a component to the end of this container. Automatically appends the provided [Separator] after the
    /// component. If `null` is provided, no [Separator] will be appended.
    ///
    /// @param component the component to add
    /// @param separator the [Separator] to append, pass `null` for no [Separator]
    /// @param entries   the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SeparatedContainer add(ContainerChildComponent component, @Nullable Separator separator, Entry... entries) {
        super.add(component, entries);
        if (separator != null) {
            super.add(separator);
        }
        return this;
    }

    /// {@inheritDoc}
    ///
    /// Automatically appends the default [Separator] after the component. Use
    /// [#add(ContainerChildComponent, Separator, Entry...)] to override.
    ///
    /// @param component the component to add
    /// @param entries   {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public SeparatedContainer addFirst(ContainerChildComponent component, Entry... entries) {
        super.addFirst(separator);
        super.addFirst(component, entries);
        return this;
    }

    /// Adds the provided element to this component as the first element of this component. Automatically appends the
    /// provided [Separator] after the component. If `null` is provided, no [Separator] will be appended.
    ///
    /// Automatically appends the default [Separator] after the component. Use
    /// [#add(ContainerChildComponent, Separator, Entry...)] to override.
    ///
    /// @param component the component to add
    /// @param separator the [Separator] to append, pass `null` for no [Separator]
    /// @param entries   the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    public SeparatedContainer addFirst(ContainerChildComponent component, @Nullable Separator separator, Entry... entries) {
        if (separator != null) {
            super.addFirst(separator);
        }
        super.addFirst(component, entries);
        return this;
    }

    /// {@inheritDoc}
    ///
    /// This method doesn't append a [Separator] and should thus be used for the last component of the container.
    ///
    /// @param component the component to add
    /// @param entries   {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public SeparatedContainer addLast(ContainerChildComponent component, Entry... entries) {
        super.addLast(component, entries);
        return this;
    }

    @Override
    public SeparatedContainer withAccentColor(@Nullable Integer accentColor) {
        container = container.withAccentColor(accentColor);
        return this;
    }

    @Override
    public SeparatedContainer withAccentColor(@Nullable Color accentColor) {
        container = container.withAccentColor(accentColor);
        return this;
    }

    @Override
    public SeparatedContainer withSpoiler(boolean spoiler) {
        container = container.withSpoiler(spoiler);
        return this;
    }

    @Override
    public SeparatedContainer withComponents(ContainerChildComponent component, ContainerChildComponent... components) {
        return withComponents(Stream.concat(Stream.of(component), Arrays.stream(components)).toList());
    }

    @Override
    public SeparatedContainer withComponents(Collection<? extends ContainerChildComponent> components) {
        container = container.withComponents(components);
        return this;
    }
}
