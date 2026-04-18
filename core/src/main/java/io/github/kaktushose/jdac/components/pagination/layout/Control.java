package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.annotations.interactions.MenuOption;
import io.github.kaktushose.jdac.annotations.interactions.StringMenu;
import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.internal.PageButtonImpl;
import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;

/// A [Control] is responsible for controlling a [Pagination]. It cannot be added directly to the [Pagination] and must
/// be instead wrapped inside a [ControlRow].
///
/// There are two types of controls:
/// ## [PageButton]
/// A [PageButton] is a [Button] that performs an arbitrary task with the [Pagination]. Most commonly, this would be
/// scrolling back and forth, but could also be something like a refresh button.
///
/// [PageButton]s can have a [Direction]. See [PageButton] for details.
///
/// When used within JDA-Commands you can use the [Component] class to reference a
/// [`Button`][io.github.kaktushose.jdac.annotations.interactions.Button] handler:
///
/// ```
/// var control = Control.forward(Component.button("onForth"));
/// ```
///
/// ## [PageSelect]
/// A [PageSelect] is a bit more restrictive than a [PageButton] in its use case. It is backed by [StringSelectMenu] and
/// can be used by the user to directly jump to a specific page.
///
/// The [SelectOption]s are generated automatically depending on the state of the [Pagination]. See [PageSelect] for
/// details.
///
/// When used within JDA-Commands you can use the [Component] class to reference a [StringMenu] handler. Please note,
/// that this [StringMenu] cannot have any own [MenuOption]s.
///
/// ```
/// var control = Control.select(Component.stringSelect("onPageSelect"));
/// ```
///
/// @param <T> the type of [ActionRowChildComponent] of this [Control]
public sealed interface Control<T extends ActionRowChildComponent> extends Threshold permits PageButton, PageSelect {

    /// Creates a new [PageButton] with [Direction#FORWARD].
    ///
    /// @param component the underlying [Button]. Can also be [Component#button(String, Entry...)]
    /// @return the new [PageButton]
    static PageButton forward(Button component) {
        return new PageButtonImpl(component, Direction.FORWARD);
    }

    /// Creates a new [PageButton] with [Direction#FORWARD] that skips the passed amount.
    ///
    /// @param component the underlying [Button]. Can also be [Component#button(String, Entry...)]
    /// @param amount    the amount to scroll forward
    /// @return the new [PageButton]
    static PageButton forward(Button component, int amount) {
        return new PageButtonImpl(component, Direction.FORWARD, amount);
    }

    /// Creates a new [PageButton] with [Direction#BACKWARD].
    ///
    /// @param component the underlying [Button]. Can also be [Component#button(String, Entry...)]
    /// @return the new [PageButton]
    static PageButton backward(Button component) {
        return new PageButtonImpl(component, Direction.BACKWARD);
    }

    /// Creates a new [PageButton] with [Direction#BACKWARD] that skips the passed amount.
    ///
    /// @param component the underlying [Button]. Can also be [Component#button(String, Entry...)]
    /// @param amount    the amount to scroll backward
    /// @return the new [PageButton]
    static PageButton backward(Button component, int amount) {
        return new PageButtonImpl(component, Direction.BACKWARD, amount);
    }

    /// Creates a new [PageButton] with [Direction#NEUTRAL].
    ///
    /// @param component the underlying [Button]. Can also be [Component#button(String, Entry...)]
    /// @return the new [PageButton]
    static PageButton neutral(Button component) {
        return new PageButtonImpl(component, Direction.NEUTRAL);
    }

    /// Creates a new [PageSelect] with the default format of `Page %d`.
    ///
    /// @param component the underlying [StringSelectMenu]. Can also be [Component#stringSelect(String, Entry...)]
    /// @return the new [PageSelect]
    static PageSelect select(StringSelectMenu component) {
        return new PageSelectImpl(component, "Page %d");
    }

    /// Creates a new [PageSelect] with a custom format. The format uses [String#format(String, Object...)] and passes
    /// the page number as an int.
    ///
    /// @param format the format to generate the [SelectOption]'s value from.
    /// @param component the underlying [StringSelectMenu]. Can also be [Component#stringSelect(String, Entry...)]
    /// @return the new [PageSelect]
    static PageSelect select(StringSelectMenu component, String format) {
        return new PageSelectImpl(component, format);
    }

    int threshold();

    /// Gets the component of this control
    ///
    /// @return [T]
    T component();

    /// The direction of a [PageButton].
    enum Direction {
        /// A forward direction means that the [PageButton] will be disabled when the last page is reached.
        FORWARD,
        /// A neutral direction means that the [PageButton] will never be disabled.
        NEUTRAL,
        /// A backward direction means that the [PageButton] will be disabled when the first page is reached.
        BACKWARD
    }
}
