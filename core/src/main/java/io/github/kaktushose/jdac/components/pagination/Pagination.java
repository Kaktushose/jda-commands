package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.internal.PaginationImpl;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.SequencedCollection;

public interface Pagination {

    static Pagination of(PaginationLayout component, PaginationLayout... components) {
        return of(Helpers.mergeVararg(component, components));
    }

    static Pagination of(SequencedCollection<PaginationLayout> components) {
        return new PaginationImpl(components);
    }

    Pagination maxPages(int maxPages);

    Pagination container(boolean container);

    default Pagination color(@Nullable Color color) {
        return color(color == null ? null : color.getRGB());
    }

    Pagination color(@Nullable Integer color);

    Pagination spoiler(boolean spoiler);

    default Pagination forward() {
        return forward(1);
    }

    Pagination forward(int amount);

    default Pagination backward() {
        return backward(1);
    }

    Pagination backward(int amount);

    Pagination page(int page);

    SequencedCollection<MessageTopLevelComponent> build();
}
