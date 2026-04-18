package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.layout.Static;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.SequencedCollection;

@ApiStatus.Internal
public record StaticImpl(SequencedCollection<ContainerChildComponent> components) implements Static {

    public StaticImpl {
        components = Collections.unmodifiableSequencedCollection(components);
    }

}
