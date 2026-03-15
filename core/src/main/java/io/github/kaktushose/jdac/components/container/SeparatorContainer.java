package io.github.kaktushose.jdac.components.container;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

/// special handling of [TextDisplayContainer]
public final class SeparatorContainer extends BaseContainer<ContainerChildComponent> {

    private final Separator separator;

    public SeparatorContainer(Resolver<String> resolver, DiscordLocale locale, ContainerChildComponent header, Separator separator) {
        this(resolver, locale.toLocale(), header, separator);
    }

    public SeparatorContainer(Resolver<String> resolver, Locale locale, ContainerChildComponent header, Separator separator) {
        super(resolver, locale, header);
        super.add(separator);
        this.separator = separator;
    }

    public static SeparatorContainer of(ContainerChildComponent header, Separator separator) {
        BaseContainer.checkAccess();
        return new SeparatorContainer(
                Introspection.scopedGet(Property.MESSAGE_RESOLVER),
                Introspection.scopedGet(Property.JDA_EVENT).getUserLocale().toLocale(),
                header,
                separator
        );
    }

    @Override
    public SeparatorContainer add(ContainerChildComponent component) {
        add(component, super::add);
        super.add(separator);
        return this;
    }

    public SeparatorContainer add(ContainerChildComponent component, @Nullable Separator separator) {
        add(component, super::add);
        if (separator != null) {
            super.add(separator);
        }
        return this;
    }

    @Override
    public SeparatorContainer addFirst(ContainerChildComponent component) {
        super.addFirst(separator);
        add(component, super::addFirst);
        return this;
    }

    /// doesn't add separator
    @Override
    public SeparatorContainer addLast(ContainerChildComponent component) {
        add(component, super::addLast);
        return this;
    }

    private void add(ContainerChildComponent component, Consumer<ContainerChildComponent> consumer) {
        if (component instanceof TextDisplayContainer textDisplay) {
            textDisplay.textDisplays().stream().map(ContainerChildComponent.class::cast).forEach(consumer);
        } else {
            consumer.accept(component);
        }
    }
}
