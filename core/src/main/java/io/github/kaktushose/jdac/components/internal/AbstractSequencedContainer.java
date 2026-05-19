package io.github.kaktushose.jdac.components.internal;

import io.github.kaktushose.jdac.components.SequencedTextDisplay;
import io.github.kaktushose.jdac.components.container.SeparatedContainer;
import io.github.kaktushose.jdac.components.container.SequencedContainer;
import io.github.kaktushose.jdac.components.container.TextDisplayContainer;
import io.github.kaktushose.jdac.exceptions.ReplyException;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.ComponentResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import net.dv8tion.jda.api.components.Component;
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
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.components.container.ContainerImpl;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public abstract sealed class AbstractSequencedContainer<E extends Component, R extends AbstractSequencedContainer<E, R>>
        implements LocalizedComponent, SequencedComponent<E>, Container, MessageTopLevelComponentUnion, SerializableData
        permits SeparatedContainer, SequencedContainer, TextDisplayContainer {

    protected final List<Entry> entries;
    private final ComponentResolver<Container> resolver;
    protected Container container;
    private Locale locale;

    public AbstractSequencedContainer(Resolver<String> resolver, Locale locale, Container container) {
        this.entries = new ArrayList<>();
        this.resolver = new ComponentResolver<>(resolver, Container.class);
        this.container = container;
        this.locale = locale;
    }

    protected abstract R self();

    // LocalizedComponent

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    public R locale(DiscordLocale locale) {
        return locale(locale.toLocale());
    }

    @Override
    public R locale(Locale locale) {
        this.locale = locale;
        return self();
    }

    @Override
    public R entries(Entry... entries) {
        return entries(Arrays.asList(entries));
    }

    @Override
    public R entries(Collection<Entry> entries) {
        this.entries.addAll(entries);
        return self();
    }

    // SequencedComponent

    @Override
    public R add(E component, Entry... entries) {
        entries(entries);
        return add(component, ArrayList::add);
    }

    @Override
    public R addFirst(E component, Entry... entries) {
        entries(entries);
        return add(component, ArrayList::addFirst);
    }

    @Override
    public R addLast(E component, Entry... entries) {
        entries(entries);
        return add(component, ArrayList::addLast);
    }

    public R addAll(Collection<E> component, Entry... entries) {
        entries(entries);
        component.forEach(this::add);
        return self();
    }

    private R add(E component, BiConsumer<ArrayList<Component>, Component> consumer) {
        var components = new ArrayList<>(container.getComponents().stream().map(Component.class::cast).toList());
        if (component instanceof SequencedTextDisplay textDisplay) {
            textDisplay.textDisplays()
                    .stream()
                    .map(Component.class::cast)
                    .forEach(it -> consumer.accept(components, it));
        } else {
            consumer.accept(components, component);
        }
        container = Container.of(components.stream().map(ContainerChildComponent.class::cast).toList());
        return self();
    }

    // SerializableData

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

    // Container

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

    @Override
    public R replace(ComponentReplacer replacer) {
        container = container.replace(replacer);
        return self();
    }

    @Override
    public R withAccentColor(@Nullable Integer accentColor) {
        container = container.withAccentColor(accentColor);
        return self();
    }

    @Override
    public R withAccentColor(@Nullable Color accentColor) {
        return withAccentColor(accentColor == null ? null : accentColor.getRGB());
    }

    @Override
    public R withSpoiler(boolean spoiler) {
        container = container.withSpoiler(spoiler);
        return self();
    }

    @Override
    public R withComponents(Collection<? extends ContainerChildComponent> components) {
        container = container.withComponents(components);
        return self();
    }

    @Override
    public R withComponents(ContainerChildComponent component, ContainerChildComponent... components) {
        return withComponents(Helpers.mergeVararg(component, components));
    }

    @Override
    public R withDisabled(boolean disabled) {
        container = container.withDisabled(disabled);
        return self();
    }

    @Override
    public R asDisabled() {
        return withDisabled(true);
    }

    @Override
    public R asEnabled() {
        return withDisabled(true);
    }

    @Override
    public R withUniqueId(int uniqueId) {
        container = container.withUniqueId(uniqueId);
        return self();
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    @Override
    public int getUniqueId() {
        return container.getUniqueId();
    }

    @Override
    public @Nullable Integer getAccentColorRaw() {
        return container.getAccentColorRaw();
    }

    @Override
    public boolean isSpoiler() {
        return container.isSpoiler();
    }

    // MessageTopLevelComponentUnion

    @Override
    public Container asContainer() {
        return container;
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

    protected static void checkAccess() {
        if (!JDACIntrospection.accessible()) {
            throw new ReplyException("outside-event-handler");
        }
    }

    private Map<String, @Nullable Object> toMap() {
        return Entry.toMap(entries.toArray(Entry[]::new));
    }
}
