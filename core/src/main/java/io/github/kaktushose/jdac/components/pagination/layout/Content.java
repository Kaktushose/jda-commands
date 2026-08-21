package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.ContentImpl;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.List;
import java.util.SequencedCollection;
import java.util.function.Function;

/// A type of [PaginationLayout] that wraps any kind of [content][ContainerChildComponent].
public sealed interface Content extends PaginationLayout permits ContentImpl {

    /// Creates a new pagination [Content] containing a [TextDisplay] with the given content.
    ///
    /// This is a shortcut for:
    /// ```
    /// Static.of(TextDisplay.of(content))
    /// ```
    ///
    /// @param content the content of the [TextDisplay]
    /// @param entries the [Entries][Entry] used for localization
    /// @return a new pagination [Content] containing a [TextDisplay]
    static Content text(String content, Entry... entries) {
        return of(List.of(TextDisplay.of(content)), entries);
    }

    /// Creates a new pagination [Content] containing the given [ContainerChildComponent]s
    ///
    /// @param component  the [ContainerChildComponent] to add to the static
    /// @param components additional [ContainerChildComponent]s to add
    /// @return a new pagination [Content] containing the given [ContainerChildComponent]s
    static Content of(ContainerChildComponent component, ContainerChildComponent... components) {
        return of(Helpers.mergeVararg(component, components));
    }

    /// Creates a new pagination [Content] containing the given [ContainerChildComponent]s
    ///
    /// @param components the [ContainerChildComponent]s to add
    /// @param entries    the [Entries][Entry] used for localization
    /// @return a new pagination [Content] containing the given [ContainerChildComponent]s
    static Content of(SequencedCollection<ContainerChildComponent> components, Entry... entries) {
        return dynamic(_ -> components, entries);
    }

    /// Creates a new pagination [Content]. The passed [Function] takes a [Page] and must return a
    /// [ContainerChildComponent] to show for the current page.
    ///
    /// @param bodyFunction the [Function] to render the current page with
    /// @param entries      the [Entries][Entry] used for localization
    /// @return a new pagination [Content]
    static Content of(Function<Page, ContainerChildComponent> bodyFunction, Entry... entries) {
        return dynamic(page -> List.of(bodyFunction.apply(page)), entries);
    }

    /// Creates a new pagination [Content]. The passed [Function] takes a [Page] and must return a [SequencedCollection]
    /// of [ContainerChildComponent]s to show for the current page.
    ///
    /// @param bodyFunction the [Function] to render the current page with
    /// @param entries      the [Entries][Entry] used for localization
    /// @return a new pagination [Content]
    static Content dynamic(Function<Page, SequencedCollection<ContainerChildComponent>> bodyFunction, Entry... entries) {
        return new ContentImpl(bodyFunction, entries);
    }

    /// Gets the [ContainerChildComponent] [Function] of this [Content].
    ///
    /// @return the [ContainerChildComponent] [Function]
    Function<Page, ? extends SequencedCollection<ContainerChildComponent>> components();
}
