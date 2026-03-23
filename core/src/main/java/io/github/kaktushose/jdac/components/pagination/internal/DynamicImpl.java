package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Dynamic;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.BiFunction;

public record DynamicImpl(
        BiFunction<Integer, Integer, SequencedCollection<ContainerChildComponent>> function,
        int threshold
) implements Dynamic {

    public DynamicImpl(BiFunction<Integer, Integer, SequencedCollection<ContainerChildComponent>> bodyFunction) {
        this(bodyFunction, 1);
    }

    @Override
    public Dynamic threshold(int threshold) {
        return new DynamicImpl(function, threshold);
    }
}
