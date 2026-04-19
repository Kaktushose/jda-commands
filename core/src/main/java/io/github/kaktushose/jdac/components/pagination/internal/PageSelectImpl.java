package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.PageSelect;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public record PageSelectImpl(
        StringSelectMenu component,
        int threshold,
        @Nullable Integer selectOptions,
        String format
) implements PageSelect {

    public PageSelectImpl(StringSelectMenu component, String format) {
        this(component, 2, null, format);
    }

    @Override
    public PageSelect selectOptions(@Nullable Integer pages) {
        return new PageSelectImpl(component, threshold, pages, format);
    }

    @Override
    public PageSelect format(String format) {
        return new PageSelectImpl(component, threshold, selectOptions, format);
    }

    @Override
    public PageSelect threshold(int threshold) {
        return new PageSelectImpl(component, threshold, selectOptions, format);
    }
}
