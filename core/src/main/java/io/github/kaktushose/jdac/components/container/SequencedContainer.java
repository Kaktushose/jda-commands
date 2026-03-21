package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.components.internal.SequencedComponent;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.components.container.ContainerImpl;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/// A [Container] implementation that allows adding [ContainerChildComponent]s sequentially.
///
/// This class is fully compatible with JDA and implements both [Container] and [MessageTopLevelComponentUnion]. Use
/// [#of(ContainerChildComponent)] to create a new [SequencedContainer]. Compared to JDA's [Container], the component
/// list of this container isn't immutable and can be extended.
/// ```
/// SequencedContainer<ContainerChildComponent> container = SequencedContainer.of(TextDisplay.of("Hello World!"));
///
/// container.add(Separator.createDivider(Spacing.SMALL));
///
/// container.add(TextDisplay.of("Goodbye World"));
/// ```
///
/// ## Localization
/// This [Container] implementation also supports localization. When [#getComponents()] or [#toData()] is called, the
/// component list of this container is localized via [ComponentResolver]. Use [#entries(Entry...)] to provide
/// additional [Entries][Entry] outside the [#add(ContainerChildComponent, Entry...)] methods.
///
/// ## Usage outside JDA-Commands
/// [Container#of(Collection)] uses the [Introspection] API to access a [Resolver] and the user locale. This means, the
/// static `of` factory method is only usable in [Stage#PREPARATION]. If you want to use this class outside JDA-Commands
/// call the constructor and pass the [Resolver] as well as the [Locale] manually.
/// ```
/// SequencedContainer<ContainerChildComponent> container = new SequencedContainer(resolver, locale, TextDisplay.of("Hello World!));
/// ```
///
/// @param <T> the component type this container supports.
public sealed class SequencedContainer<T extends ContainerChildComponent>
        extends AbstractComponentImpl
        implements SequencedComponent<T>, Container, MessageTopLevelComponentUnion
        permits TextDisplayContainer, SeparatorContainer {

    protected final List<Entry> entries;
    private final ComponentResolver<Container> resolver;
    protected int uniqueId;
    protected Container container;
    private Locale locale;

    /// Constructs a new [SequencedContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first component of this container
    public SequencedContainer(Resolver<String> resolver, DiscordLocale locale, T header) {
        this(resolver, locale.toLocale(), header);
    }

    /// Constructs a new [SequencedContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first component of this container
    public SequencedContainer(Resolver<String> resolver, Locale locale, T header) {
        this.resolver = new ComponentResolver<>(resolver, Container.class);
        this.locale = locale;
        entries = new ArrayList<>();
        uniqueId = -1;
        container = Container.of(header);
    }

    /// Constructs a new [SequencedContainer] from the given component [T].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param <T> the component type this container supports.
    /// @throws IllegalStateException if the [Stage#PREPARATION] isn't accessible.
    public static <T extends ContainerChildComponent> SequencedContainer<T> of(T header) {
        checkAccess();
        return new SequencedContainer<>(
                Introspection.scopedGet(Property.MESSAGE_RESOLVER),
                Introspection.scopedGet(Property.JDA_EVENT).getUserLocale().toLocale(),
                header
        );
    }

    protected static void checkAccess() {
        if (!Introspection.accessible()) {
            throw new IllegalStateException("TODO: Illegal call outside of of event handler");
        }
    }

    @Override
    public SequencedContainer<T> add(T component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.add((ContainerChildComponentUnion) component);
        container = Container.of(components);
        return this;
    }

    @Override
    public SequencedContainer<T> addFirst(T component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.addFirst((ContainerChildComponentUnion) component);
        container = Container.of(components);
        return this;
    }

    @Override
    public SequencedContainer<T> addLast(T component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.addLast((ContainerChildComponentUnion) component);
        container = Container.of(components);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequencedContainer<T> addAll(Collection<T> component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.addAll((Collection<ContainerChildComponentUnion>) component);
        container = Container.of(components);
        return this;
    }

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    public SequencedContainer<T> locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public SequencedContainer<T> entries(Collection<Entry> entries) {
        this.entries.addAll(entries);
        return this;
    }

    /// {@inheritDoc}
    ///
    /// Localizes all components of this container before returning the list.
    ///
    /// @return {@inheritDoc}
    @Override
    public @Unmodifiable List<ContainerChildComponentUnion> getComponents() {
        container = resolver.resolve(container, locale, toMap());
        return container.getComponents();
    }

    /// {@inheritDoc}
    ///
    /// Localizes all components of this container before returning the [DataObject].
    ///
    /// @return {@inheritDoc}
    @Override
    public DataObject toData() {
        container = resolver.resolve(container, locale, toMap());
        return ((ContainerImpl) container).toData();
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    @Override
    public int getUniqueId() {
        return uniqueId;
    }

    @Override
    public SequencedContainer<T> withUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    @Override
    public SequencedContainer<T> replace(ComponentReplacer replacer) {
        container = container.replace(replacer);
        return this;
    }

    /// This method is not supported and will always throw [UnsupportedOperationException].
    @Override
    public ActionRow asActionRow() {
        throw new UnsupportedOperationException();
    }

    /// This method is not supported and will always throw [UnsupportedOperationException].
    @Override
    public Section asSection() {
        throw new UnsupportedOperationException();
    }

    /// This method is not supported and will always throw [UnsupportedOperationException].
    @Override
    public TextDisplay asTextDisplay() {
        throw new UnsupportedOperationException();
    }

    /// This method is not supported and will always throw [UnsupportedOperationException].
    @Override
    public MediaGallery asMediaGallery() {
        throw new UnsupportedOperationException();
    }

    /// This method is not supported and will always throw [UnsupportedOperationException].
    @Override
    public Separator asSeparator() {
        throw new UnsupportedOperationException();
    }

    /// This method is not supported and will always throw [UnsupportedOperationException].
    @Override
    public FileDisplay asFileDisplay() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Container asContainer() {
        return container;
    }

    @Override
    public SequencedContainer<T> withAccentColor(@Nullable Integer accentColor) {
        container = container.withAccentColor(accentColor);
        return this;
    }

    @Override
    public SequencedContainer<T> withSpoiler(boolean spoiler) {
        container = container.withSpoiler(spoiler);
        return this;
    }

    @Override
    public SequencedContainer<T> withComponents(ContainerChildComponent component, ContainerChildComponent... components) {
        return withComponents(Stream.concat(Stream.of(component), Arrays.stream(components)).toList());
    }

    @Override
    public SequencedContainer<T> withComponents(Collection<? extends ContainerChildComponent> components) {
        container = container.withComponents(components);
        return this;
    }

    @Override
    public @Nullable Integer getAccentColorRaw() {
        return container.getAccentColorRaw();
    }

    @Override
    public boolean isSpoiler() {
        return container.isSpoiler();
    }

    private Map<String, @Nullable Object> toMap() {
        return Entry.toMap(entries.toArray(Entry[]::new));
    }
}
