package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.BodyImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.Function;

public interface Body extends PaginationLayout {

    static Body of(Function<Integer, SequencedCollection<ContainerChildComponent>> bodyFunction) {
        return new BodyImpl(bodyFunction);
    }
}
