package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import org.jspecify.annotations.Nullable;

/// If [Pagination#maxPages()] is set, this will be the upper limit, else the [Pagination#currentPage()]. This can
/// be overwritten by [PageSelect#pages(Integer)]. However, if the amount of [SelectOption]s exceeds in any case
/// [StringSelectMenu#OPTIONS_MAX_AMOUNT], this value will be used instead.
///
///
/// Use [PageSelect#format(String)] to change the value of the [SelectOption]. The default is `Page %d`.
public sealed interface PageSelect extends Control<StringSelectMenu> permits PageSelectImpl {

    @Nullable Integer pages();

    PageSelect pages(@Nullable Integer pages);

    String format();

    PageSelect format(String format);

}
