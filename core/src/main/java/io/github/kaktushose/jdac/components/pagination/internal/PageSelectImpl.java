package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.layout.Control;
import io.github.kaktushose.jdac.components.pagination.layout.PageSelect;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

@ApiStatus.Internal
public record PageSelectImpl(
        StringSelectMenu component,
        Predicate<Page> predicate,
        @Nullable Integer selectOptions,
        String format
) implements PageSelect {

    public PageSelectImpl(StringSelectMenu component, String format) {
        this(component, _ -> true, null, format);
    }

    @Override
    public PageSelect selectOptions(@Nullable Integer pages) {
        return new PageSelectImpl(component, _ -> true, pages, format);
    }

    @Override
    public PageSelect format(String format) {
        return new PageSelectImpl(component, predicate, selectOptions, format);
    }

    @Override
    public Control predicate(Predicate<Page> predicate) {
        return new PageSelectImpl(component, predicate, selectOptions, format);
    }
}
