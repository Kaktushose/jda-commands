package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.DynamicImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.BiFunction;

public non-sealed interface Dynamic extends PaginationLayout, Threshold {

    static Dynamic of(BiFunction<Integer, Integer, SequencedCollection<ContainerChildComponent>> bodyFunction) {
        return new DynamicImpl(bodyFunction);
    }

    @Override
    Dynamic threshold(int threshold);

    BiFunction<Integer, Integer, SequencedCollection<ContainerChildComponent>> function();
}
