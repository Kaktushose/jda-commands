package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.components.SequencedTextDisplay;
import io.github.kaktushose.jdac.components.internal.AbstractSequencedContainer;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.container.ContainerImpl;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
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
/// [Container#of(Collection)] uses the [JDACIntrospection] API to access a [Resolver] and the user locale. This means, the
/// static `of` factory method is only usable in [JDACScope#PREPARATION]. If you want to use this class outside JDA-Commands
/// call the constructor and pass the [Resolver] as well as the [Locale] manually.
/// ```
/// SequencedContainer<ContainerChildComponent> container = new SequencedContainer(resolver, locale, TextDisplay.of("Hello World!"));
/// ```
///
/// @param <E> the component type this container supports.
public sealed class SequencedContainer<E extends ContainerChildComponent>
        extends AbstractSequencedContainer<E, SequencedContainer<E>>
        permits TextDisplayContainer, SeparatedContainer {

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
    public SequencedContainer(Resolver<String> resolver, DiscordLocale locale, E header) {
        this(resolver, locale.toLocale(), header);
    }

    /// Constructs a new [SequencedContainer].
    ///
    /// @param resolver the [Resolver] to use for localization
    /// @param locale   the locale this container will be localized to
    /// @param header   the first component of this container
    public SequencedContainer(Resolver<String> resolver, Locale locale, E header) {
        this.resolver = new ComponentResolver<>(resolver, Container.class);
        this.locale = locale;
        entries = new ArrayList<>();
        uniqueId = -1;
        if (header instanceof SequencedTextDisplay textDisplay) {
            container = Container.of(textDisplay.textDisplays());
        } else {
            container = Container.of(header);
        }
    }

    /// Constructs a new [SequencedContainer] from the given component [T].
    ///
    /// This method can only be used inside events or in methods annotated with [IntrospectionAccess].
    ///
    /// @param <T> the component type this container supports.
    /// @throws IllegalStateException if the [JDACScope#PREPARATION] isn't accessible.
    public static <T extends ContainerChildComponent> SequencedContainer<T> of(T header) {
        checkAccess();
        return new SequencedContainer<>(
                JDACProperty.MESSAGE_RESOLVER.scopedGet(),
                JDACProperty.JDA_EVENT.scopedGet().getUserLocale().toLocale(),
                header
        );
    }

    protected static void checkAccess() {
        if (!JDACIntrospection.accessible()) {
            throw new IllegalStateException("TODO: Illegal call outside of of event handler");
        }
    }

    @Override
    public SequencedContainer<E> add(E component, Entry... entries) {
        entries(entries);
        return add(component, ArrayList::add);
    }

    @Override
    public SequencedContainer<E> addFirst(E component, Entry... entries) {
        entries(entries);
        return add(component, ArrayList::addFirst);
    }

    @Override
    public SequencedContainer<E> addLast(E component, Entry... entries) {
        entries(entries);
        return add(component, ArrayList::addLast);
    }

    @Override
    public SequencedContainer<E> addAll(Collection<E> component, Entry... entries) {
        entries(entries);
        component.forEach(this::add);
        return this;
    }

    private SequencedContainer<E> add(E component, BiConsumer<ArrayList<ContainerChildComponent>, ContainerChildComponent> consumer) {
        var components = new ArrayList<>(container.getComponents().stream().map(ContainerChildComponent.class::cast).toList());
        if (component instanceof SequencedTextDisplay textDisplay) {
            textDisplay.textDisplays().stream()
                    .map(ContainerChildComponent.class::cast)
                    .forEach(it -> consumer.accept(components, it));
        } else {
            consumer.accept(components, component);
        }
        container = Container.of(components);
        return this;
    }

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    protected SequencedContainer<E> self() {
        return this;
    }

    @Override
    public SequencedContainer<E> locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public SequencedContainer<E> entries(Collection<Entry> entries) {
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
    public SequencedContainer<E> replace(ComponentReplacer replacer) {
        container = container.replace(replacer);
        return this;
    }

    @Override
    public Container asContainer() {
        return container;
    }

    @Override
    public SequencedContainer<E> withComponents(ContainerChildComponent component, ContainerChildComponent... components) {
        return withComponents(Stream.concat(Stream.of(component), Arrays.stream(components)).toList());
    }

    @Override
    public SequencedContainer<E> withComponents(Collection<? extends ContainerChildComponent> components) {
        container = container.withComponents(components);
        return this;
    }

    private Map<String, @Nullable Object> toMap() {
        return Entry.toMap(entries.toArray(Entry[]::new));
    }
}
