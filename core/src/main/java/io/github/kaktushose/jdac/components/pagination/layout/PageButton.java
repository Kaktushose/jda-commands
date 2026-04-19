package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import net.dv8tion.jda.api.components.buttons.Button;

/// A [PageButton] is backed by a [Button] that performs an arbitrary task with a [Pagithenation]. Most commonly, this
/// would be scrolling back and forth, but could also be something like a refresh button.
///
/// [PageButton]s can have a [Direction]. When the direction is set to [Direction#BACKWARD], the [PageButton] will
/// automatically be disabled on the first page. [Direction#FORWARD] will disable the [PageButton] on the last page.
/// See [Pagination] for details on how to set the last page. [Direction#NEUTRAL] will keep the [PageButton] always
/// enabled.
///
/// When used within JDA-Commands you can use the [Component] class to reference a
/// [`Button`][io.github.kaktushose.jdac.annotations.interactions.Button] handler:
///
/// ```
/// var control = Control.forward(Component.button("onForth"));
/// ```
public sealed interface PageButton extends Control<Button> permits PageButtonImpl {

    /// Gets the [Direction] of this button.
    ///
    /// @return the [Direction]
    Direction direction();

    /// Sets the [Direction] of this button.
    ///
    /// @param direction the new [Direction]
    /// @return this instance for fluent interface
    PageButton direction(Direction direction);

    /// Gets the number of pages this button scrolls back or forth
    ///
    /// @return the number of pages this button scrolls back or forth
    int amount();

    /// Sets the number of pages this button scrolls back or forth
    ///
    /// @param amount the number of pages
    /// @return this instance for fluent interface
    PageButton amount(int amount);

}
