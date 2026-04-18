package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.internal.PageSelectImpl;
import org.jspecify.annotations.Nullable;

public sealed interface PageSelect extends Control permits PageSelectImpl {

    @Nullable Integer pages();

    PageSelect pages(@Nullable Integer pages);

    String format();

    PageSelect format(String format);

}
