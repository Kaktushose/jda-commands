package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.internal.PaginationImpl;
import io.github.kaktushose.jdac.components.pagination.layout.Body;
import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import io.github.kaktushose.jdac.components.pagination.layout.PageInfo;
import io.github.kaktushose.jdac.components.pagination.layout.Static;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator.Spacing;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.SequencedCollection;
import java.util.function.Function;

public interface Pagination {

    static Pagination of(PaginationLayout component, PaginationLayout... components) {
        return of(Helpers.mergeVararg(component, component));
    }

    static Pagination of(SequencedCollection<PaginationLayout> components) {
        return new PaginationImpl(components);
    }

    Pagination container(boolean container);

    default Pagination color(@Nullable Color color) {
        return color(color == null ? null : color.getRGB());
    }

    Pagination color(@Nullable Integer color);

    Pagination spoiler(boolean spoiler);
}
