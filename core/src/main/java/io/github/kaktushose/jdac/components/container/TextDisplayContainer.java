package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.utils.ComponentSerializer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.textdisplay.TextDisplayImpl;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public final class TextDisplayContainer extends TextDisplayImpl {

    private static final ComponentSerializer componentSerializer = new ComponentSerializer();
    private BaseContainer<TextDisplay> container;

    public TextDisplayContainer(Resolver<String> resolver, DiscordLocale locale, String header) {
        this(resolver, locale.toLocale(), header);
    }

    public TextDisplayContainer(Resolver<String> resolver, DiscordLocale locale, TextDisplay header) {
        this(resolver, locale.toLocale(), header);
    }

    public TextDisplayContainer(Resolver<String> resolver, Locale locale, String header) {
        this(resolver, locale, TextDisplay.of(header));
    }

    public TextDisplayContainer(Resolver<String> resolver, Locale locale, TextDisplay header) {
        super(componentSerializer.serialize(header));
        container = new BaseContainer<>(resolver, locale, header);
    }

    public static TextDisplayContainer of(String content) {
        return of(TextDisplay.of(content));
    }

    public static TextDisplayContainer of(TextDisplay header) {
        BaseContainer.checkAccess();
        return new TextDisplayContainer(
                Introspection.scopedGet(Property.MESSAGE_RESOLVER),
                Introspection.scopedGet(Property.JDA_EVENT).getUserLocale().toLocale(),
                header
        );
    }

    public TextDisplayContainer add(String content) {
        container.add(TextDisplay.of(content));
        return this;
    }

    public TextDisplayContainer addFirst(String content) {
        container.addFirst(TextDisplay.of(content));
        return this;
    }

    public TextDisplayContainer addLast(String content) {
        container.addLast(TextDisplay.of(content));
        return this;
    }

    public Locale locale() {
        return container.locale();
    }

    public TextDisplayContainer locale(Locale locale) {
        container.locale(locale);
        return this;
    }

    public TextDisplayContainer locale(DiscordLocale locale) {
        container.locale(locale);
        return this;
    }

    public Collection<TextDisplay> textDisplays() {
        return container.getComponents().stream().map(TextDisplay.class::cast).toList();
    }

    @Override
    public DataObject toData() {
        DataObject json = DataObject.empty().put("type", getType().getKey()).put("content", getContent());
        if (getUniqueId() >= 0) {
            json.put("id", getUniqueId());
        }
        return json;
    }

    @Override
    public String getContent() {
        return container.getComponents().stream()
                .map(TextDisplay.class::cast)
                .map(TextDisplay::getContent)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public TextDisplayContainer withContent(String content) {
        return withContent(TextDisplay.of(content));
    }

    public TextDisplayContainer withContent(TextDisplay textDisplay) {
        container = container.withComponents(textDisplay);
        return this;
    }

    @Override
    public TextDisplayContainer withUniqueId(int uniqueId) {
        return (TextDisplayContainer) super.withUniqueId(uniqueId);
    }
}
