package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.PageInfoImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.function.BiFunction;

public interface PageInfo extends PaginationLayout {

    static PageInfo of(BiFunction<Integer, Integer, ContainerChildComponent> pageInfoFunction) {
        return new PageInfoImpl(pageInfoFunction);
    }

    PageInfo threshold(int threshold);
}
