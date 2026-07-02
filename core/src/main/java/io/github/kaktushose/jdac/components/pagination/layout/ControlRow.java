package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.ControlRowImpl;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.SequencedCollection;

/// A type of [PaginationLayout] that can hold [Control]s. You can think of it like [ActionRow].
///
/// Just like with [ActionRow], one [ControlRow] can support up to five [PageButton]s but only one [PageSelect] per row.
public non-sealed interface ControlRow extends PaginationLayout {

    /// Creates a new [ControlRow].
    ///
    /// @param control  the [Control] to add to the row
    /// @param controls additional [Control]s to add to the row
    /// @return the new [ControlRow]
    @SafeVarargs
    static ControlRow of(Control<? extends ActionRowChildComponent> control, Control<? extends ActionRowChildComponent>... controls) {
        return of(Helpers.mergeVararg(control, controls));
    }

    /// Creates a new [ControlRow].
    ///
    /// @param controls the [Control]s to add to the row
    /// @return the new [ControlRow]
    static ControlRow of(SequencedCollection<Control<? extends ActionRowChildComponent>> controls) {
        return new ControlRowImpl(controls);
    }

    /// Gets all [Control]s of this [ControlRow].
    ///
    /// @return an unmodifiable [SequencedCollection] of all [Control]s
    SequencedCollection<Control<? extends ActionRowChildComponent>> controls();
}
