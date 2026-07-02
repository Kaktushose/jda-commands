package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.ContentImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.SequencedCollection;
import java.util.function.Function;

/// A type of [PaginationLayout] that wraps any kind of [content][ContainerChildComponent].
public sealed interface Content extends PaginationLayout permits ContentImpl {

    /// Gets the [ContainerChildComponent] [Function] of this [Content].
    ///
    /// @return the [ContainerChildComponent] [Function]
    Function<Page, ? extends SequencedCollection<ContainerChildComponent>> components();

    /// Creates a new pagination [Content] containing a [TextDisplay] with the given content.
    ///
    /// This is a shortcut for:
    /// ```
    /// Static.of(TextDisplay.of(content))
    /// ```
    ///
    /// @param content the content of the [TextDisplay]
    /// @return a new pagination [Content] containing a [TextDisplay]
    static Content text(String content) {
        return of(TextDisplay.of(content));
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
    /// @return a new pagination [Content] containing the given [ContainerChildComponent]s
    static Content of(SequencedCollection<ContainerChildComponent> components) {
        return of(_ -> components);
    }

    /// Creates a new pagination [Content]. The passed [Function] takes a [Page] and must return a [SequencedCollection] of
    /// [ContainerChildComponent]s to show for the current page.
    ///
    /// @param bodyFunction the [Function] to render the current page with
    /// @return a new pagination [Content]
    static Content of(Function<Page, SequencedCollection<ContainerChildComponent>> bodyFunction) {
        return new ContentImpl(bodyFunction);
    }
}
