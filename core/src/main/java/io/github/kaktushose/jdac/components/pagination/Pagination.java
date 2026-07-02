package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.internal.PaginationImpl;
import io.github.kaktushose.jdac.components.pagination.layout.Content;
import io.github.kaktushose.jdac.components.pagination.layout.Control;
import io.github.kaktushose.jdac.components.pagination.layout.Control.Direction;
import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import io.github.kaktushose.jdac.dispatching.events.ReplyableEvent;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.SequencedCollection;

/// A pagination that is based on components v2 and is controlled by action components.
///
/// ## Layout
/// A pagination is made up of [PaginationLayout]s. Each [PaginationLayout] can be placed freely and as many times as
/// wanted. The only limit is [Message#MAX_CONTENT_LENGTH_COMPONENT_V2].
///
/// By default, the pagination will be wrapped inside a [Container]. This can be disabled by calling [#container(boolean)].
/// Call [#spoiler(boolean)] and [#color(Color)] to customize the [Container].
///
/// ## Reply
/// When used inside JDA-Commands, just pass the [Pagination] to [ReplyableEvent#reply(Pagination, Entry...)].
/// [Localization][MessageResolver] will work just as with any other message.
///
/// When used outside JDA-Commands, call [#build()] to retrieve a collection of [MessageTopLevelComponent]s to send.
/// Please note, that these will not be localized.
///
/// ## Controls
/// The pagination is controlled by the instance itself. Use methods like [#forward()] or [#backward()] to paginate back
/// and forth and just reply with the same instance again.
/// ```
/// @Button("Back")
/// public void onBack(ComponentEvent event) {
///     event.reply(pagination.backward());
/// }
/// ```
/// Controls that were added via [Control#forward(Button)] or [Control#backward(Button)] will automatically be disabled
/// when the first or last page (see below) is reached. [Control#select(StringSelectMenu)] will automatically be
/// populated with [SelectOption]s.
///
/// ## Maximum Page
/// By default, the pagination has no last page. It is possible to scroll forward indefinitely. Use [#maxPages(int)] to
/// set a limit. Use [Page#cancel()] to set the maximum number of pages dynamically when paginating.
///
/// ## Predicate
/// [Content], [ControlRow]s as well as their [Control]s can have a [PaginationLayout#predicate()()] that must be matched
/// before they show up.
/// ```
/// Pagination.of(
///     ControlRow.of(...).predicate(page -> condition) // only show this control row if the condition is met
/// );
/// ```
public interface Pagination {

    /// Constructs a new [Pagination].
    ///
    /// @param component  the first [PaginationLayout] of this [Pagination]
    /// @param components additional [PaginationLayout]s
    /// @return a sendable [Pagination] instance
    static Pagination of(PaginationLayout component, PaginationLayout... components) {
        return of(Helpers.mergeVararg(component, components));
    }

    /// Constructs a new [Pagination].
    ///
    /// @param components a collection of [PaginationLayout]s of this [Pagination]
    /// @return a sendable [Pagination] instance
    static Pagination of(SequencedCollection<PaginationLayout> components) {
        return new PaginationImpl(components);
    }

    /// Sets the maximum number of pages this pagination has. When the last page is reached, all controls with
    /// [Direction#FORWARD] will be disabled.
    ///
    /// @param maxPages the maximum number of pages
    /// @return this instance for fluent interface
    Pagination maxPages(int maxPages);

    /// Sets whether this pagination should be wrapped inside a [Container].
    ///
    /// @param container `true` if this pagination should be wrapped inside a [Container]
    /// @return this instance for fluent interface
    Pagination container(boolean container);

    /// Sets the color of the container. Only has an effect if [#container(boolean)] is set to `true`.
    ///
    /// @param color the color of the container
    /// @return this instance for fluent interface
    /// @see Container#withAccentColor(Color)
    default Pagination color(@Nullable Color color) {
        return color(color == null ? null : color.getRGB());
    }

    /// Sets the color of the container. Only has an effect if [#container(boolean)] is set to `true`.
    ///
    /// @param color the color of the container
    /// @return this instance for fluent interface
    /// @see Container#withAccentColor(Integer)
    Pagination color(@Nullable Integer color);

    /// Whether the container should be marked as a spoiler. Only has an effect if [#container(boolean)] is set to `true`.
    ///
    /// @param spoiler the new spoiler status
    /// @return this instance for fluent interface
    /// @see Container#withSpoiler(boolean)
    Pagination spoiler(boolean spoiler);

    /// Sets the current page of this pagination to the first page.
    ///
    /// This is equivalent to calling [`pagination.page(1)`][#page(int)].
    ///
    /// @return this instance for fluent interface
    default Pagination firstPage() {
        return page(1);
    }

    /// Scrolls this pagination one page forth.
    ///
    /// Throws an exception if [#maxPages(int)] is set and the [#currentPage()] is already the last page.
    ///
    /// @return this instance for fluent interface
    default Pagination forward() {
        return forward(1);
    }

    /// Scrolls this pagination forth for the passed amount.
    ///
    /// Throws an exception if [#maxPages(int)] is set and the [#currentPage()] is already the last page.
    ///
    /// @param amount the number of pages to scroll forward
    /// @return this instance for fluent interface
    Pagination forward(int amount);

    /// Scrolls this pagination one page back.
    ///
    /// Throws an exception if the [#currentPage()] is the first page.
    ///
    /// @return this instance for fluent interface
    default Pagination backward() {
        return backward(1);
    }

    /// Scrolls this pagination back for the passed amount.
    ///
    /// Throws an exception if the [#currentPage()] is the first page.
    ///
    /// @param amount the number of pages to scroll back
    /// @return this instance for fluent interface
    Pagination backward(int amount);

    /// Sets the current page of this pagination to the first page.
    ///
    /// This method does nothing, if [Pagination#maxPages(int)] isn't set!
    ///
    /// @return this instance for fluent interface
    default Pagination lastPage() {
        if (maxPages() != null) {
            return page(maxPages());
        }
        return this;
    }

    /// Sets the current page of this pagination to the passed page.
    ///
    /// Throws an exception if [#maxPages(int)] is set and the passed page exceeds this limit.
    ///
    /// @param page the page to jump to provided as a String
    /// @return this instance for fluent interface
    /// @throws NumberFormatException if the String conversion fails
    default Pagination page(String page) {
        return page(Integer.parseInt(page));
    }

    /// Sets the current page of this pagination to the passed page.
    ///
    /// Throws an exception if [#maxPages(int)] is set and the passed page exceeds this limit.
    ///
    /// @param page the page to jump to
    /// @return this instance for fluent interface
    /// @implNote the first page begins at index `1`
    Pagination page(int page);

    /// Gets the current page of this pagination.
    ///
    /// @return the current page
    int currentPage();

    /// Gets the maximum number of pages of this pagination. Will return `null` if not set via [#maxPages()]
    ///
    /// @return possibly null number of maximum pages
    @Nullable Integer maxPages();

    /// Builds this pagination into a collection of [MessageTopLevelComponent]s that can be sent.
    ///
    /// Will automatically disable controls and populate select menus as described in the class level documentation.
    ///
    /// @return a [SequencedCollection] of [MessageTopLevelComponent]s representing this pagination.
    SequencedCollection<MessageTopLevelComponent> build();
}
