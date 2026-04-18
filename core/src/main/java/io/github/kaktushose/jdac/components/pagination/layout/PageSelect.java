package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import org.jspecify.annotations.Nullable;

public sealed interface PageSelect extends Control<StringSelectMenu> permits PageSelectImpl {

    @Nullable Integer pages();

    PageSelect pages(@Nullable Integer pages);

    String format();

    PageSelect format(String format);

}
