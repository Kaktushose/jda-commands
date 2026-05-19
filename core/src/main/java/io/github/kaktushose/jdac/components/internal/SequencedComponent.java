package io.github.kaktushose.jdac.components.internal;

import io.github.kaktushose.jdac.components.SequencedTextDisplay;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.Component;

import java.util.Collection;
import java.util.Locale;

/// An interface for [Component]s that allow adding its elements in sequence.
///
/// ## Example
/// ```
/// SequencedComponent<ContainerChildComponent> container = SequencedContainer.of(TextDisplay.of("Hello World!"));
///
/// container.add(Separator.createDivider(Spacing.SMALL));
///
/// container.add(TextDisplay.of("Goodbye World"));
/// ```
///
/// @param <T> the component type this container supports.
public sealed interface SequencedComponent<T extends Component>
        extends LocalizedComponent
        permits SequencedTextDisplay, AbstractSequencedContainer {

    /// Appends the provided element to the end of this component.
    ///
    /// @param component the element to add
    /// @param entries   the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    SequencedComponent<T> add(T component, Entry... entries);

    /// Adds the provided element to this component as the first element of this component.
    ///
    /// @param component the element to add
    /// @param entries   the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    SequencedComponent<T> addFirst(T component, Entry... entries);

    /// Adds the provided element to this component as the last element of this component.
    ///
    /// @param component the element to add
    /// @param entries   the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    SequencedComponent<T> addLast(T component, Entry... entries);

    /// Appends all the elements in the specified collection to the end of this component.
    ///
    /// @param component the elements to add
    /// @param entries   the [Entries][Entry] used for localization
    /// @return this instance for fluent interface
    SequencedComponent<T> addAll(Collection<T> component, Entry... entries);

}
