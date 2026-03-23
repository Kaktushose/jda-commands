package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.PageInfo;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.function.BiFunction;

public record PageInfoImpl(
        BiFunction<Integer, Integer, ContainerChildComponent> pageInfoFunction,
        int threshold
) implements PageInfo {

    public PageInfoImpl(BiFunction<Integer, Integer, ContainerChildComponent> pageInfoFunction) {
        this(pageInfoFunction, 2);
    }

    @Override
    public PageInfo threshold(int threshold) {
        return new io.github.kaktushose.jdac.components.pagination.internal.PageInfoImpl(pageInfoFunction, threshold);
    }
}
