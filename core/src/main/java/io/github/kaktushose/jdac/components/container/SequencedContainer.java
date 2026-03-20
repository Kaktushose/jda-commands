package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.introspection.Introspection;
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

public sealed class SequencedContainer<T extends ContainerChildComponent>
        extends AbstractComponentImpl
        implements Container, MessageTopLevelComponentUnion
        permits SeparatorContainer {

    protected final List<Entry> entries;
    private final ComponentResolver<Container> resolver;
    protected int uniqueId;
    protected Container container;
    private Locale locale;

    public SequencedContainer(Resolver<String> resolver, DiscordLocale locale, T header) {
        this(resolver, locale.toLocale(), header);
    }

    public SequencedContainer(Resolver<String> resolver, Locale locale, T header) {
        this.resolver = new ComponentResolver<>(resolver, Container.class);
        this.locale = locale;
        entries = new ArrayList<>();
        uniqueId = -1;
        container = Container.of(header);
    }

    protected static void checkAccess() {
        if (!Introspection.accessible()) {
            throw new IllegalStateException("TODO: Illegal call outside of of event handler");
        }
    }

    public SequencedContainer<T> add(T component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.add((ContainerChildComponentUnion) component);
        container = Container.of(components);
        return this;
    }

    public SequencedContainer<T> addFirst(T component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.addFirst((ContainerChildComponentUnion) component);
        container = Container.of(components);
        return this;
    }

    public SequencedContainer<T> addLast(T component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.addLast((ContainerChildComponentUnion) component);
        container = Container.of(components);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SequencedContainer<T> addAll(T... component) {
        var components = new ArrayList<>(container.getComponents());
        components.addAll((Collection<ContainerChildComponentUnion>) List.of(component));
        container = Container.of(components);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SequencedContainer<T> addAll(Collection<T> component, Entry... entries) {
        entries(entries);
        var components = new ArrayList<>(container.getComponents());
        components.addAll((Collection<ContainerChildComponentUnion>) component);
        container = Container.of(components);
        return this;
    }

    public Locale locale() {
        return locale;
    }

    public SequencedContainer<T> locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public SequencedContainer<T> locale(DiscordLocale locale) {
        this.locale = locale.toLocale();
        return this;
    }

    public SequencedContainer<T> entries(Entry... entry) {
        entries.addAll(Arrays.asList(entry));
        return this;
    }

    public SequencedContainer<T> entries(Collection<Entry> entries) {
        this.entries.addAll(entries);
        return this;
    }

    @Override
    public @Unmodifiable List<ContainerChildComponentUnion> getComponents() {
        if (!Introspection.accessible()) {
            container = resolver.resolve(container, locale, toMap());
        }
        return container.getComponents();
    }

    @Override
    public DataObject toData() {
        if (!Introspection.accessible()) {
            container = resolver.resolve(container, locale, toMap());
        }
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

    @Override
    public ActionRow asActionRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Section asSection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextDisplay asTextDisplay() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MediaGallery asMediaGallery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Separator asSeparator() {
        throw new UnsupportedOperationException();
    }

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
