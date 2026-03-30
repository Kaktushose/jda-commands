package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.layout.Dynamic;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.BiFunction;
import java.util.function.Function;

public record DynamicImpl(
        Function<Page, SequencedCollection<ContainerChildComponent>> function,
        int threshold
) implements Dynamic {

    public DynamicImpl(Function<Page, SequencedCollection<ContainerChildComponent>> bodyFunction) {
        this(bodyFunction, 1);
    }

    @Override
    public Dynamic threshold(int threshold) {
        return new DynamicImpl(function, threshold);
    }
}
