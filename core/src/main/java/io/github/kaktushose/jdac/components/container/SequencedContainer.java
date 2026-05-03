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
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Collection;
import java.util.Locale;

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
public final class SequencedContainer<E extends ContainerChildComponent>
        extends AbstractSequencedContainer<E, SequencedContainer<E>> {

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
        Container container;
        if (header instanceof SequencedTextDisplay textDisplay) {
            container = Container.of(textDisplay.textDisplays());
        } else {
            container = Container.of(header);
        }
        super(resolver, locale, container);
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

    @Override
    protected SequencedContainer<E> self() {
        return this;
    }
}
