package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.annotations.interactions.MenuOption;
import io.github.kaktushose.jdac.annotations.interactions.StringMenu;
import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import org.jspecify.annotations.Nullable;

/// A [PageSelect] is backed by [StringSelectMenu] and can be used by the user to directly jump to a specific page.
///
/// The [SelectOption]s are generated automatically depending on the state of the [Pagination]. If set,
/// [Pagination#maxPages()] will determine the number of [SelectOption]s generated, else [Pagination#currentPage()]
/// This can be overwritten by [PageSelect#selectOptions(Integer)]. However, if in any case the number of
/// [SelectOption]s exceeds [StringSelectMenu#OPTIONS_MAX_AMOUNT], this value will be used instead.
///
/// When used within JDA-Commands you can use the [Component] class to reference a [StringMenu] handler. Please note,
/// that this [StringMenu] cannot have any own [MenuOption]s.
///
/// ```
/// var control = Control.select(Component.stringSelect("onPageSelect"));
/// ```
///
/// The [SelectOption]'s label will be generated using Java's [String#format(String, Object...)] method with a format
/// String and the current page number. The default format String is `Page %d`. Use [PageSelect#format(String)] to change the
/// format String. The [SelectOption]'s value will always just be the page number.
public sealed interface PageSelect extends Control<StringSelectMenu> permits PageSelectImpl {

    /// Gets the number of [SelectOption]s to generate, or `null` if not set.
    ///
    /// @return the possibly null number of [SelectOption]s to generate
    @Nullable Integer selectOptions();

    /// Sets the number of [SelectOption]s to generate. Set to `null` to automatically determine this number as explained
    /// int the class docs.
    ///
    /// @param pages the number of [SelectOption]s to generate
    /// @return this instance for fluent interface
    PageSelect selectOptions(@Nullable Integer pages);

    /// Gets the format to generate the [SelectOption]s label with. Uses [String#format(String, Object...)] and passes the page
    /// number as an int. The default value is `Page %d`.
    ///
    /// @return the format to generate the [SelectOption]'s value from
    String format();

    /// Sets the format to generate the [SelectOption]s label with. Uses [String#format(String, Object...)] and passes the page
    /// number as an int. The default value is `Page %d`.
    ///
    /// @param format the format to generate the [SelectOption]'s value from
    /// @return this instance for fluent interface
    PageSelect format(String format);

}
