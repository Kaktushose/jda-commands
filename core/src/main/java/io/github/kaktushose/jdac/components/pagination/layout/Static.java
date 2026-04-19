package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.StaticImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.separator.Separator.Spacing;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.SequencedCollection;

/// A type of [PaginationLayout] that is as the name says *static*. This means it always remains exactly the same,
/// independent of the pagination state (e.g. current page number).
///
/// This can be any [ContainerChildComponent].
public non-sealed interface Static extends PaginationLayout {

    /// Creates a new [Static] containing a [Separator].
    ///
    /// This is a shortcut for:
    /// ```
    /// Static.of(Separator.createDivider(spacing));
    /// ```
    ///
    /// @param spacing the [Spacing] of the [Separator]
    /// @return a new [Static] containing a [Separator]
    static Static divider(Spacing spacing) {
        return of(Separator.createDivider(spacing));
    }

    /// Creates a new [Static] containing an invisible [Separator].
    ///
    /// This is a shortcut for:
    /// ```
    /// Static.of(Separator.createInvisible(spacing));
    /// ```
    ///
    /// @param spacing the [Spacing] of the invisible [Separator]
    /// @return a new [Static] containing a [Separator]
    static Static spacing(Spacing spacing) {
        return of(Separator.createInvisible(spacing));
    }

    /// Creates a new [Static] containing a [TextDisplay] with the given content.
    ///
    /// This is a shortcut for:
    /// ```
    /// Static.of(TextDisplay.of(content))
    /// ```
    ///
    /// @param content the content of the [TextDisplay]
    /// @return a new [Static] containing a [TextDisplay]
    static Static text(String content) {
        return of(TextDisplay.of(content));
    }

    /// Creates a new [Static] containing the given [ContainerChildComponent]s
    ///
    /// @param component  the [ContainerChildComponent] to add to the static
    /// @param components additional [ContainerChildComponent]s to add
    /// @return a new [Static] containing the given [ContainerChildComponent]s
    static Static of(ContainerChildComponent component, ContainerChildComponent... components) {
        return of(Helpers.mergeVararg(component, components));
    }

    /// Creates a new [Static] containing the given [ContainerChildComponent]s
    ///
    /// @param components the [ContainerChildComponent]s to add
    /// @return a new [Static] containing the given [ContainerChildComponent]s
    static Static of(SequencedCollection<ContainerChildComponent> components) {
        return new StaticImpl(components);
    }

    /// Gets all [ContainerChildComponent]s of this [Static].
    ///
    /// @return an unmodifiable [SequencedCollection] of all [ContainerChildComponent]s
    SequencedCollection<ContainerChildComponent> components();
}
