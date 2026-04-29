package io.github.kaktushose.jdac.components.internal;

import io.github.kaktushose.jdac.components.container.SeparatedContainer;
import io.github.kaktushose.jdac.components.container.SequencedContainer;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.SerializableData;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@SuppressWarnings("NullableProblems")
public abstract sealed class AbstractSequencedContainer<E extends Component, R extends AbstractSequencedContainer<E, R>>
        implements LocalizedComponent, SequencedComponent<E>, Container, MessageTopLevelComponentUnion, SerializableData
        permits SequencedContainer, SeparatedContainer {

    protected abstract R self();

    // LocalizedComponent

    @Override
    public R locale(DiscordLocale locale) {
        return locale(locale.toLocale());
    }

    @Override
    public abstract R locale(Locale locale);

    @Override
    public R entries(Entry... entries) {
        return entries(Arrays.asList(entries));
    }

    @Override
    public abstract R entries(Collection<Entry> entries);

    // SequencedComponent

    @Override
    public abstract R add(E component, Entry... entries);

    @Override
    public abstract R addFirst(E component, Entry... entries);

    @Override
    public abstract R addLast(E component, Entry... entries);

    @Override
    public abstract R addAll(Collection<E> component, Entry... entries);

    // Container

    @Override
    public abstract R replace(ComponentReplacer replacer);

    @Override
    public R withAccentColor(@Nullable Integer accentColor) {
        asContainer().withAccentColor(accentColor);
        return self();
    }

    @Override
    public R withAccentColor(@Nullable Color accentColor) {
        return withAccentColor(accentColor == null ? null : accentColor.getRGB());
    }

    @Override
    public R withSpoiler(boolean spoiler) {
        asContainer().withSpoiler(spoiler);
        return self();
    }

    @Override
    public abstract R withComponents(Collection<? extends ContainerChildComponent> components);

    @Override
    public abstract R withComponents(ContainerChildComponent component, ContainerChildComponent... components);

    @Override
    public R withDisabled(boolean disabled) {
        asContainer().withDisabled(disabled);
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
        asContainer().withUniqueId(uniqueId);
        return self();
    }

    @Override
    public @Nullable Integer getAccentColorRaw() {
        return asContainer().getAccentColorRaw();
    }

    @Override
    public boolean isSpoiler() {
        return asContainer().isSpoiler();
    }

    @Override
    public boolean isMessageCompatible() {
        return Container.super.isMessageCompatible();
    }

    @Override
    public boolean isModalCompatible() {
        return Container.super.isModalCompatible();
    }

    @Override
    public boolean isDisabled() {
        return Container.super.isDisabled();
    }

    @Override
    public boolean isEnabled() {
        return Container.super.isEnabled();
    }

    @Override
    public @Nullable Color getAccentColor() {
        return Container.super.getAccentColor();
    }

    // MessageTopLevelComponentUnion

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

}
